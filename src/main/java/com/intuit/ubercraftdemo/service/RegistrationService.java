package com.intuit.ubercraftdemo.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.endpoint.driver.DriverRegistrationDTO;
import com.intuit.ubercraftdemo.endpoint.driver.RegistrationAcknowledgementDTO;
import com.intuit.ubercraftdemo.exception.InvalidStepModificationException;
import com.intuit.ubercraftdemo.exception.NoSuchRecordException;
import com.intuit.ubercraftdemo.model.*;
import com.intuit.ubercraftdemo.model.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

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
            DriverOnboardingStep driverOnboardingStep = getDriverOnboardingStep(
                    onboardingStepTemplate, driver, driverOnboardingProcess);
            driverOnboardingSteps.add(driverOnboardingStep);
        }
        driverOnboardingStepRepository.saveAll(driverOnboardingSteps);

        return new RegistrationAcknowledgementDTO(driverRegistration.getUsername(),
                driverRegistration.getOperatingCountry(), driverRegistration.getOperatingState(),
                driverRegistration.getOperatingCity());
    }

    private DriverOnboardingStep getDriverOnboardingStep(
            OnboardingStepTemplate onboardingStepTemplate, Driver driver,
            DriverOnboardingProcess driverOnboardingProcess) {

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
        return driverOnboardingStep;
    }

    @Transactional
    public Map<String, String> uploadDocumentsAndUpdateFileIdInOnboardingStep(Integer driverId,
                                                                              MultipartFile... files) throws IOException {

        Optional<DriverOnboardingProcess> driverOnboardingProcess = driverOnboardingProcessRepository.findByDriverId(
                driverId);

        List<DriverOnboardingStep> driverOnboardingSteps = driverOnboardingStepRepository.findAllByDriverId(
                driverId);
        Optional<DriverOnboardingStep> documentUploadOnboardingStep = driverOnboardingSteps.stream()
                .filter(driverOnboardingStep -> "Document verification" .equals(
                        driverOnboardingStep.getStepName())).findFirst();

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

        List<BudgetEditionS3> s3FileRecords = new ArrayList<>();
        for (MultipartFile file : files) {
            BudgetEditionS3 s3FileRecord = new BudgetEditionS3();
            s3FileRecord.setFileContent(file.getBytes());
            s3FileRecord.setName(file.getName());
            s3FileRecord.setOriginalFilename(file.getOriginalFilename());
            s3FileRecords.add(s3FileRecord);
        }
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> onboardingStepAttachments = gson.fromJson(
                documentUploadOnboardingStep.get().getAttachments(), type);

        Iterable<BudgetEditionS3> uploadedS3Files = budgetEditionS3Repository.saveAll(
                s3FileRecords);

        Map<String, String> documentNameToFileName = new HashMap<>();
        uploadedS3Files.forEach(file -> {
            documentNameToFileName.put(file.getName(), file.getOriginalFilename());
            onboardingStepAttachments.put(file.getName(), file.getId().toString());
        });
        documentUploadOnboardingStep.get().setStatus(StepStatus.WaitingForAuditorAssignment);
        documentUploadOnboardingStep.get().setAttachments(gson.toJson(onboardingStepAttachments));
        driverOnboardingStepRepository.save(documentUploadOnboardingStep.get());
        return documentNameToFileName;
    }
}
