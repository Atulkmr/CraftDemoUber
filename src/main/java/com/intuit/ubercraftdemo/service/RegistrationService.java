package com.intuit.ubercraftdemo.service;

import com.google.gson.Gson;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.endpoint.driver.DriverRegistrationDTO;
import com.intuit.ubercraftdemo.endpoint.driver.RegistrationAcknowledgementDTO;
import com.intuit.ubercraftdemo.exception.NoSuchRecordException;
import com.intuit.ubercraftdemo.model.Driver;
import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.OnboardingProcessTemplate;
import com.intuit.ubercraftdemo.model.OnboardingStepTemplate;
import com.intuit.ubercraftdemo.model.OperationMarket;
import com.intuit.ubercraftdemo.model.Vehicle;
import com.intuit.ubercraftdemo.model.repository.BudgetEditionS3Repository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingProcessRepository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingStepRepository;
import com.intuit.ubercraftdemo.model.repository.DriverRepository;
import com.intuit.ubercraftdemo.model.repository.OnboardingProcessTemplateRepository;
import com.intuit.ubercraftdemo.model.repository.OnboardingStepTemplateRepository;
import com.intuit.ubercraftdemo.model.repository.OperationMarketRepository;
import com.intuit.ubercraftdemo.model.repository.VehicleRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Slf4j
public class RegistrationService {

	private final Gson gson;
	private final DriverRepository driverRepository;
	private final VehicleRepository vehicleRepository;
	private final OperationMarketRepository operationMarketRepository;
	private final OnboardingProcessTemplateRepository onboardingProcessTemplateRepository;
	private final OnboardingStepTemplateRepository onboardingStepTemplateRepository;
	private final DriverOnboardingStepRepository driverOnboardingStepRepository;
	private final DriverOnboardingProcessRepository driverOnboardingProcessRepository;
	private final BudgetEditionS3Repository budgetEditionS3Repository;
	private final DriverMapper driverMapper;

	@Transactional
	public RegistrationAcknowledgementDTO createNewDriverAndRelatedOnboardingRecords(
		DriverRegistrationDTO driverRegistration) {
		Optional<Vehicle> vehicle = vehicleRepository.findByMakeModelYearColour(
			driverRegistration.getVehicle().getMake(), driverRegistration.getVehicle().getModel(),
			driverRegistration.getVehicle().getYear(), driverRegistration.getVehicle().getColour());
		if (vehicle.isEmpty()) {
			throw new NoSuchRecordException(Vehicle.class.getName());
		}

		Optional<OperationMarket> operationMarket = operationMarketRepository.findByCountryStateCity(
			driverRegistration.getOperatingCountry(), driverRegistration.getOperatingState(),
			driverRegistration.getOperatingCity());
		if (operationMarket.isEmpty()) {
			throw new NoSuchRecordException(OperationMarket.class.getName());
		}

		Driver driver = driverMapper.toEntity(driverRegistration);
		driver.setVehicleId(vehicle.get().getId());
		driver.setOperationMarketId(operationMarket.get().getId());
		driver = driverRepository.save(driver);

		Optional<OnboardingProcessTemplate> onboardingProcessTemplate = onboardingProcessTemplateRepository.findByOperationMarketIdAndProductCategory(
			operationMarket.get().getId(), vehicle.get().getDefaultProductCategoryId());
		List<OnboardingStepTemplate> onboardingStepTemplates = onboardingStepTemplateRepository.findByProcessTemplateId(
			onboardingProcessTemplate.get().getId());

		DriverOnboardingProcess driverOnboardingProcess = new DriverOnboardingProcess();
		driverOnboardingProcess.setDriverId(driver.getId());
		driverOnboardingProcess.setOnboardingProcessTemplateId(
			onboardingProcessTemplate.get().getId());
		driverOnboardingProcess.setProductCategoryId(vehicle.get().getDefaultProductCategoryId());
		driverOnboardingProcess.setProcessName(onboardingProcessTemplate.get().getProcessName());
		driverOnboardingProcess.setCurrentStepNumber(1);
		driverOnboardingProcess = driverOnboardingProcessRepository.save(driverOnboardingProcess);

		List<DriverOnboardingStep> driverOnboardingSteps = new ArrayList<>();
		for (OnboardingStepTemplate onboardingStepTemplate : onboardingStepTemplates) {
			DriverOnboardingStep driverOnboardingStep = new DriverOnboardingStep();
			driverOnboardingStep.setDriverId(driver.getId());
			driverOnboardingStep.setDriverOnboardingProcessId(driverOnboardingProcess.getId());
			driverOnboardingStep.setStepName(onboardingStepTemplate.getStepName());
			driverOnboardingStep.setAttachments(onboardingStepTemplate.getAttachments());
			driverOnboardingStep.setOnboardingStepTemplateId(onboardingStepTemplate.getId());
			driverOnboardingStep.setStepNumber(onboardingStepTemplate.getSequenceNumber());
			driverOnboardingStep.setStatus(onboardingStepTemplate.getInitialStatus());
			driverOnboardingStep.setCreatedDate(new Date());
			driverOnboardingStep.setLastModifiedDate(new Date());
			driverOnboardingSteps.add(driverOnboardingStep);
		}
		driverOnboardingStepRepository.saveAll(driverOnboardingSteps);

		return new RegistrationAcknowledgementDTO(driverRegistration.getUsername(),
			driverRegistration.getOperatingCountry(), driverRegistration.getOperatingState(),
			driverRegistration.getOperatingCity());
	}
}
