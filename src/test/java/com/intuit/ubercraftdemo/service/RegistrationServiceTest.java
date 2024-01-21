package com.intuit.ubercraftdemo.service;

import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.endpoint.driver.DriverRegistrationDTO;
import com.intuit.ubercraftdemo.exception.NoSuchRecordException;
import com.intuit.ubercraftdemo.model.*;
import com.intuit.ubercraftdemo.model.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RegistrationServiceTest {

    @InjectMocks
    RegistrationService registrationService;

    @Mock
    DriverRepository driverRepository;

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    OperationMarketRepository operationMarketRepository;

    @Mock
    OnboardingProcessTemplateRepository onboardingProcessTemplateRepository;

    @Mock
    OnboardingStepTemplateRepository onboardingStepTemplateRepository;

    @Mock
    DriverOnboardingStepRepository driverOnboardingStepRepository;

    @Mock
    DriverOnboardingProcessRepository driverOnboardingProcessRepository;

    @Mock
    DriverMapper driverMapper;

    @Test
    void createNewDriverAndRelatedOnboardingRecords_vehicleNotFound_throwNoSuchRecordException() {
        DriverRegistrationDTO driverRegistrationDTO = new DriverRegistrationDTO();
        driverRegistrationDTO.setVehicle(new DriverRegistrationDTO.VehicleDTO());

        when(vehicleRepository.findByMakeModelYearColour(any(), any(), any(), any())).thenReturn(Optional.empty());

        assertThrows(NoSuchRecordException.class,
                () -> registrationService.createNewDriverAndRelatedOnboardingRecords(driverRegistrationDTO));
    }

    @Test
    void createNewDriverAndRelatedOnboardingRecords_driverRegistered_success() {
        DriverRegistrationDTO driverRegistrationDTO = new DriverRegistrationDTO();
        driverRegistrationDTO.setVehicle(new DriverRegistrationDTO.VehicleDTO());
        driverRegistrationDTO.setUsername("username");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1);

        OperationMarket operationMarket = new OperationMarket();
        operationMarket.setId(1);

        Driver driver = new Driver();

        when(vehicleRepository.findByMakeModelYearColour(any(), any(), any(), any()))
                .thenReturn(Optional.of(vehicle));

        when(operationMarketRepository.findByCountryStateCity(any(), any(), any()))
                .thenReturn(Optional.of(operationMarket));

        when(driverMapper.toEntity(any(DriverRegistrationDTO.class))).thenReturn(driver);

        when(driverRepository.save(any())).thenReturn(driver);

        when(onboardingProcessTemplateRepository.findByOperationMarketIdAndProductCategory(any(), any()))
                .thenReturn(Optional.of(new OnboardingProcessTemplate()));

        when(onboardingStepTemplateRepository.findByProcessTemplateId(any()))
                .thenReturn(Collections.emptyList());

        registrationService.createNewDriverAndRelatedOnboardingRecords(driverRegistrationDTO);

        verify(vehicleRepository, times(1))
                .findByMakeModelYearColour(any(), any(), any(), any());
        verify(operationMarketRepository, times(1)).findByCountryStateCity(any(), any(), any());
        verify(driverRepository, times(1)).save(any());
    }
}