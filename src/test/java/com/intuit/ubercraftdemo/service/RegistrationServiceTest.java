package com.intuit.ubercraftdemo.service;

import com.google.gson.Gson;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.endpoint.driver.DriverRegistrationDTO;
import com.intuit.ubercraftdemo.exception.InvalidStepModificationException;
import com.intuit.ubercraftdemo.exception.NoSuchRecordException;
import com.intuit.ubercraftdemo.model.*;
import com.intuit.ubercraftdemo.model.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Type;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
    BudgetEditionS3Repository budgetEditionS3Repository;
    @Mock
    Gson gson;

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


    @Test
    public void testUploadDocumentsAndUpdateFileIdInOnboardingStepThrowsExceptionIfStepIsNotCurrent() {

        Integer driverId = 1;
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt","text/plain", "test data".getBytes());
        DriverOnboardingProcess dop = new DriverOnboardingProcess();
        dop.setDriverId(driverId);
        dop.setCurrentStepNumber(2);
        DriverOnboardingStep dos = new DriverOnboardingStep();
        dos.setDriverId(driverId);
        dos.setStepNumber(1);
        dos.setStepName("Not Document verification");
        dos.setStatus(StepStatus.DriverActionNeeded);
        List<DriverOnboardingStep> driverOnboardingStepList = new ArrayList<>();

        when(driverOnboardingProcessRepository.findByDriverId(eq(driverId))).thenReturn(Optional.of(dop));
        when(driverOnboardingStepRepository.findAllByDriverId(eq(driverId))).thenReturn(driverOnboardingStepList);

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> registrationService.uploadDocumentsAndUpdateFileIdInOnboardingStep(driverId, file));
    }
//    @Test
//    public void testUploadDocumentsAndUpdateFileIdInOnboardingStepIsValid() throws Exception {
//
//        Integer driverId = 1;
//        MockMultipartFile file = new MockMultipartFile("file", "filename.txt","text/plain", "test data".getBytes());
//        DriverOnboardingProcess dop = new DriverOnboardingProcess();
//        dop.setDriverId(driverId);
//        dop.setCurrentStepNumber(1);
//        DriverOnboardingStep dos = new DriverOnboardingStep();
//        dos.setDriverId(driverId);
//        dos.setStepNumber(1);
//        dos.setAttachments("name");
//        dos.setStepName("Document verification");
//        dos.setStatus(StepStatus.DriverActionNeeded);
//        BudgetEditionS3 s3FileRecord = new BudgetEditionS3();
//        s3FileRecord.setId(1);
//        s3FileRecord.setName("name");
//        s3FileRecord.setOriginalFilename("filename");
//        s3FileRecord.setFileContent(new byte[10]);
//        Map<String, String> attachments = new HashMap<>();
//
//        when(driverOnboardingProcessRepository.findByDriverId(eq(driverId))).thenReturn(Optional.of(dop));
//        when(driverOnboardingStepRepository.findAllByDriverId(eq(driverId))).thenReturn(Arrays.asList(dos));
//        when(budgetEditionS3Repository.saveAll(any())).thenReturn(Arrays.asList(s3FileRecord));
//        when(gson.fromJson(any(String.class), any(Type.class))).thenReturn(attachments);
//
//        Map<String, String> result = registrationService.uploadDocumentsAndUpdateFileIdInOnboardingStep(driverId, file);
//        assertThat(result.values()).containsOnly(file.getOriginalFilename());
//    }
//


//    @Test
//    public void testUploadDocumentsAndUpdateFileIdInOnboardingStepThrowsExceptionIfStatusIsNotDriverActionNeeded() {
//        Integer driverId = 1;
//        MockMultipartFile file = new MockMultipartFile("file", "filename.txt","text/plain", "test data".getBytes());
//        DriverOnboardingProcess dop = new DriverOnboardingProcess();
//        dop.setDriverId(driverId);
//        dop.setCurrentStepNumber(2);
//        DriverOnboardingStep dos = new DriverOnboardingStep();
//        dos.setDriverId(driverId);
//        dos.setStepNumber(1);
//        dos.setStepName("Document verification");
//        dos.setStatus(StepStatus.WaitingForAuditorAssignment);
//        List<DriverOnboardingStep> driverOnboardingStepList = new ArrayList<>();
//
//        when(driverOnboardingProcessRepository.findByDriverId(eq(driverId))).thenReturn(Optional.of(dop));
//        when(driverOnboardingStepRepository.findAllByDriverId(eq(driverId))).thenReturn(driverOnboardingStepList);
//
//        assertThatExceptionOfType(InvalidStepModificationException.class)
//                .isThrownBy(() -> registrationService.uploadDocumentsAndUpdateFileIdInOnboardingStep(driverId, file));
//    }
}