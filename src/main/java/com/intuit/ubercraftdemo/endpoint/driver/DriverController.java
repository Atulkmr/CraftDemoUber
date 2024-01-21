package com.intuit.ubercraftdemo.endpoint.driver;

import com.google.gson.Gson;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.exception.InvalidDriverStatusTransitionException;
import com.intuit.ubercraftdemo.exception.InvalidFileTypeException;
import com.intuit.ubercraftdemo.model.Driver;
import com.intuit.ubercraftdemo.model.Driver.DriverStatus;
import com.intuit.ubercraftdemo.model.repository.*;
import com.intuit.ubercraftdemo.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/driver")
@AllArgsConstructor
public class DriverController {


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
    private final RegistrationService registrationService;

    @PostMapping("/onboard/register")
    public ResponseEntity<RegistrationAcknowledgementDTO> registerAsNewDriver(
            @RequestBody DriverRegistrationDTO driverRegistration) {
        RegistrationAcknowledgementDTO acknowledgementDTO = registrationService.createNewDriverAndRelatedOnboardingRecords(
                driverRegistration);
        return ResponseEntity.ok(acknowledgementDTO);
    }


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

        Map<String, String> documentNameToFileName = registrationService.uploadDocumentsAndUpdateFileIdInOnboardingStep(
                driverId, aadhar, drivingLicense);

        return ResponseEntity.ok(documentNameToFileName);
    }

    @PatchMapping(path = "{driverId}/status/toggle")
    public ResponseEntity<Map<String, String>> toggleStatus(
            @PathVariable("driverId") Integer driverId) {

        Map<String, String> updatedStatusKeyValue =
                registrationService.validateAndToggleDriverStatus(driverId);
        return ResponseEntity.ok(updatedStatusKeyValue);
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
