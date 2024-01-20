package com.intuit.ubercraftdemo.endpoint.driver;

import com.google.gson.Gson;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.exception.InvalidDriverStatusTransitionException;
import com.intuit.ubercraftdemo.exception.InvalidFileTypeException;
import com.intuit.ubercraftdemo.exception.InvalidStepModificationException;
import com.intuit.ubercraftdemo.model.BudgetEditionS3;
import com.intuit.ubercraftdemo.model.Driver;
import com.intuit.ubercraftdemo.model.Driver.DriverStatus;
import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.OnboardingProcessTemplate;
import com.intuit.ubercraftdemo.model.OnboardingStepTemplate;
import com.intuit.ubercraftdemo.model.OperationMarket;
import com.intuit.ubercraftdemo.model.StepStatus;
import com.intuit.ubercraftdemo.model.Vehicle;
import com.intuit.ubercraftdemo.model.repository.BudgetEditionS3Repository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingProcessRepository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingStepRepository;
import com.intuit.ubercraftdemo.model.repository.DriverRepository;
import com.intuit.ubercraftdemo.model.repository.OnboardingProcessTemplateRepository;
import com.intuit.ubercraftdemo.model.repository.OnboardingStepTemplateRepository;
import com.intuit.ubercraftdemo.model.repository.OperationMarketRepository;
import com.intuit.ubercraftdemo.model.repository.VehicleRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/driver")
@AllArgsConstructor
@Slf4j
public class DriverController {


	private final Gson gson;
	private DriverRepository driverRepository;
	private VehicleRepository vehicleRepository;
	private OperationMarketRepository operationMarketRepository;
	private OnboardingProcessTemplateRepository onboardingProcessTemplateRepository;
	private OnboardingStepTemplateRepository onboardingStepTemplateRepository;
	private DriverOnboardingStepRepository driverOnboardingStepRepository;
	private DriverOnboardingProcessRepository driverOnboardingProcessRepository;
	private BudgetEditionS3Repository budgetEditionS3Repository;
	private DriverMapper driverMapper;

	@PostMapping("/onboard/register")
	@Transactional
	public ResponseEntity<Driver> saveEntity(@RequestBody DriverDTO driverRegistration) {
		log.trace("Beginning of save for Driver record");
		Optional<Vehicle> vehicle = vehicleRepository.findByMakeModelYearColour(
			driverRegistration.getVehicle().getMake(), driverRegistration.getVehicle().getModel(),
			driverRegistration.getVehicle().getYear(), driverRegistration.getVehicle().getColour());
		//TODO If this car isn't acceptable. Reject
		Optional<OperationMarket> operationMarket = operationMarketRepository.findByCountryStateCity(
			driverRegistration.getOperatingCountry(), driverRegistration.getOperatingState(),
			driverRegistration.getOperatingCity());
		//TODO If this market isn't available. Reject.
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
		driverOnboardingProcess.setProcessName(
			driver.getUsername() + onboardingProcessTemplate.get().getProcessName());
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

		return ResponseEntity.ok(driver);
	}


	@Transactional
	@PostMapping(value = "onboard/{driverId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	//TODO Remove this path variable by obtaining the driverId from the SecurityContext.
	public ResponseEntity<Map<String, String>> uploadDocuments(
		@PathVariable("driverId") Integer driverId, @RequestPart("aadhar") MultipartFile aadhar,
		@RequestPart("drivingLicense") MultipartFile drivingLicense) throws IOException {

		List<String> whitelistedFileTypes = List.of(
			MediaType.APPLICATION_PDF_VALUE,
			MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE);

		checkForWhitelistedMediaType(whitelistedFileTypes, aadhar, drivingLicense);

		Optional<DriverOnboardingProcess> driverOnboardingProcess = driverOnboardingProcessRepository.findByDriverId(
			driverId);

		List<DriverOnboardingStep> driverOnboardingSteps = driverOnboardingStepRepository.findAllByDriverId(
			driverId);
		Optional<DriverOnboardingStep> documentUploadOnboardingStep = driverOnboardingSteps.stream()
			.filter(driverOnboardingStep -> driverOnboardingStep.getOnboardingStepTemplateId() == 1)
			.findFirst();

		if (documentUploadOnboardingStep.isEmpty()) {
			//This isn't possible as long as Driver and satellite audit records are created together.
			throw new RuntimeException();
		}

		//If the onboarding process's current step number isn't equal to the step number of document upload step
		//it means either a prior step is pending, or this step was already completed.
		if (!documentUploadOnboardingStep.get().getStepNumber()
			.equals(driverOnboardingProcess.get().getCurrentStepNumber())) {
			Integer currentStepNumber = driverOnboardingProcess.get().getCurrentStepNumber();
			DriverOnboardingStep currentlyActiveStep = driverOnboardingSteps.stream()
				.filter(step -> step.getOnboardingStepTemplateId() == currentStepNumber).findFirst()
				.get();
			throw new InvalidStepModificationException(currentlyActiveStep,
				documentUploadOnboardingStep.get());
		}

		//If the document upload step's status isn't DriverActionNeeded, then this isn't the driver's turn to upload documents.
		if (!documentUploadOnboardingStep.get().getStatus().equals(StepStatus.DriverActionNeeded)) {
			throw new InvalidStepModificationException(
				documentUploadOnboardingStep.get().getStatus(), StepStatus.DriverActionNeeded);
		}
		BudgetEditionS3 aadharFile = new BudgetEditionS3();
		aadharFile.setFileContent(aadhar.getBytes());
		aadharFile.setName(aadhar.getName());
		aadharFile.setOriginalFilename(aadhar.getOriginalFilename());

		BudgetEditionS3 drivingLicenseFile = new BudgetEditionS3();
		drivingLicenseFile.setFileContent(drivingLicense.getBytes());
		drivingLicenseFile.setName(drivingLicense.getName());
		drivingLicenseFile.setOriginalFilename(drivingLicense.getOriginalFilename());

		Map<String, String> dosAttachments = gson.fromJson(
			documentUploadOnboardingStep.get().getAttachments(), Map.class);
		Iterable<BudgetEditionS3> uploadedFiles = budgetEditionS3Repository.saveAll(
			List.of(aadharFile, drivingLicenseFile));
		Map<String, String> fileNameToFileId = new HashMap<>();
		uploadedFiles.forEach(file -> {
			fileNameToFileId.put(file.getName(), file.getOriginalFilename());
			dosAttachments.put(file.getName(), file.getId().toString());
		});
		documentUploadOnboardingStep.get().setStatus(StepStatus.WaitingForAuditorAssignment);
		documentUploadOnboardingStep.get().setAttachments(gson.toJson(dosAttachments));
		driverOnboardingStepRepository.save(documentUploadOnboardingStep.get());

		return ResponseEntity.ok(fileNameToFileId);
	}

	@PatchMapping(path = "{driverId}/status/toggle")
	public ResponseEntity<Pair<String, String>> toggleStatus(
		@PathVariable("driverId") Integer driverId) {

		Optional<Driver> driverOptional = driverRepository.findById(driverId);
		if (driverOptional.isEmpty()) {
			//This isn't a possible scenario if we obtain the driverId from the auth jwt.
			// But doing this for good measure.
			throw new RuntimeException();
		}
		Driver driver = driverOptional.get();
		//If the current status is onboarding, driver's availability status can't be toggled
		if (DriverStatus.ONBOARDING.equals(driver.getStatus())) {
			throw new InvalidDriverStatusTransitionException();
		}

		driver.setStatus(DriverStatus.READY_TO_WORK.equals(driver.getStatus()) ?
			DriverStatus.WORKING : DriverStatus.READY_TO_WORK);
		//toggle the status and save the record.
		driverRepository.save(driver);

		//Return the newly set status as a key-value pair.
		return ResponseEntity.ok(Pair.of("status", driver.getStatus().toString()));
	}

	private void checkForWhitelistedMediaType(List<String> whitelistedFileTypes,
		MultipartFile... suppliedFiles) {
		for (MultipartFile suppliedFile : suppliedFiles) {
			if (!whitelistedFileTypes.contains(suppliedFile.getContentType())) {
				throw new InvalidFileTypeException(whitelistedFileTypes,
					suppliedFile.getContentType());
			}
		}
	}
}
