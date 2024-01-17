package com.intuit.ubercraftdemo.endpoint;

import com.intuit.ubercraftdemo.BudgetEditionS3Repository;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.DriverOnboardingProcessRepository;
import com.intuit.ubercraftdemo.DriverOnboardingStepRepository;
import com.intuit.ubercraftdemo.DriverRepository;
import com.intuit.ubercraftdemo.OnboardingProcessTemplateRepository;
import com.intuit.ubercraftdemo.OnboardingStepTemplateRepository;
import com.intuit.ubercraftdemo.OperationMarketRepository;
import com.intuit.ubercraftdemo.VehicleRepository;
import com.intuit.ubercraftdemo.model.BudgetEditionS3;
import com.intuit.ubercraftdemo.model.Driver;
import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.OnboardingProcessTemplate;
import com.intuit.ubercraftdemo.model.OnboardingStepTemplate;
import com.intuit.ubercraftdemo.model.OperationMarket;
import com.intuit.ubercraftdemo.model.Vehicle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class DriverController {

	private DriverRepository driverRepository;
	private VehicleRepository vehicleRepository;
	private OperationMarketRepository operationMarketRepository;
	private OnboardingProcessTemplateRepository onboardingProcessTemplateRepository;
	private OnboardingStepTemplateRepository onboardingStepTemplateRepository;
	private DriverOnboardingStepRepository driverOnboardingStepRepository;
	private DriverOnboardingProcessRepository driverOnboardingProcessRepository;
	private BudgetEditionS3Repository budgetEditionS3Repository;
	private DriverMapper driverMapper;

	@PostMapping("/driver/register")
	@Transactional
	public ResponseEntity<Driver> saveEntity(@RequestBody DriverDTO driverRegistration) {
		Optional<Vehicle> vehicle = vehicleRepository.findByMakeModelYearColour(
			driverRegistration.getVehicle().getMake(), driverRegistration.getVehicle().getModel(),
			driverRegistration.getVehicle().getYear(), driverRegistration.getVehicle().getColour());

		Optional<OperationMarket> operationMarket = operationMarketRepository.findByCountryStateCity(
			driverRegistration.getOperatingCountry(), driverRegistration.getOperatingState(),
			driverRegistration.getOperatingCity());

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
		driverOnboardingProcess.setProcessName(driver.getUsername() +
			onboardingProcessTemplate.get().getProcessName());
		driverOnboardingProcess.setCurrentStepNumber(1);
		driverOnboardingProcess = driverOnboardingProcessRepository.save(driverOnboardingProcess);

		List<DriverOnboardingStep> driverOnboardingSteps = new ArrayList<>();
		for (OnboardingStepTemplate onboardingStepTemplate : onboardingStepTemplates) {
			DriverOnboardingStep driverOnboardingStep = new DriverOnboardingStep();
			driverOnboardingStep.setDriverId(driver.getId());
			driverOnboardingStep.setDriverOnboardingProcessId(driverOnboardingProcess.getId());
			driverOnboardingStep.setStepName(onboardingStepTemplate.getStepName());
			driverOnboardingStep.setOnboardingStepTemplateId(onboardingStepTemplate.getId());
			driverOnboardingStep.setSequenceNumber(onboardingStepTemplate.getSequenceNumber());
			driverOnboardingStep.setStatus(onboardingStepTemplate.getInitialStatus());
			driverOnboardingStep.setCreatedDate(new Date());
			driverOnboardingStep.setLastModifiedDate(new Date());
			driverOnboardingSteps.add(driverOnboardingStep);
		}

		Iterable<DriverOnboardingStep> saved = driverOnboardingStepRepository.saveAll(
			driverOnboardingSteps);

		if (driverOnboardingProcess.getId() != null) {
			throw new RuntimeException();
		}

		return ResponseEntity.ok(driver);
	}


	@Transactional
	@PostMapping(value = "/driver/document/upload")
	public ResponseEntity<String> uploadDocuments(@RequestParam("aadhar") MultipartFile aadhar,
		@RequestPart("drivingLicense") MultipartFile drivingLicense) throws IOException {
		BudgetEditionS3 aadharFile = new BudgetEditionS3();
		aadharFile.setFileContent(aadhar.getBytes());
		aadharFile = budgetEditionS3Repository.save(aadharFile);
		throw new RuntimeException();
	}
}
