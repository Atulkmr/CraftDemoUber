package com.intuit.ubercraftdemo.endpoint.auditor;

import com.google.gson.Gson;
import com.intuit.ubercraftdemo.model.repository.AuditorOnboardingStepRepository;
import com.intuit.ubercraftdemo.model.repository.BudgetEditionS3Repository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingProcessRepository;
import com.intuit.ubercraftdemo.model.repository.DriverOnboardingStepRepository;
import com.intuit.ubercraftdemo.exception.NoCaseAssignedException;
import com.intuit.ubercraftdemo.model.AuditorOnboardingStep;
import com.intuit.ubercraftdemo.model.BudgetEditionS3;
import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/auditor/document-verification-case")
public class DocumentAuditCaseController{

	//TODO the auditor username is hardcoded right now. Ideally it should be obtained from the auth token.
	private final static String auditorUsername = "kyc-auditor@gmail.com";

	private final DriverOnboardingStepRepository driverOnboardingStepRepository;
	private final DriverOnboardingProcessRepository driverOnboardingProcessRepository;
	private final AuditorOnboardingStepRepository auditorOnboardingStepRepository;
	private final BudgetEditionS3Repository budgetEditionS3Repository;
	private final Gson gson;

	@Transactional
	@PatchMapping("/assign")
	public ResponseEntity<String> findAndAssignOldestDocumentAuditCase() {
		List<AuditorOnboardingStep> auditorOnboardingStepsForThisUser = auditorOnboardingStepRepository.findAllByUsername(
			auditorUsername);
		Optional<DriverOnboardingStep> caseToAssign = driverOnboardingStepRepository.findOldestDriverOnboardingStep(
			auditorOnboardingStepsForThisUser.stream()
				.map(AuditorOnboardingStep::getAssignedOnboardingStepId).collect(
					Collectors.toList()), StepStatus.WaitingForAuditorAssignment);
		if (caseToAssign.isEmpty()) {
			return ResponseEntity.ok("Nothing left to do.");
		}
		caseToAssign.get().setStatus(StepStatus.Processing);
		caseToAssign.get().setAssignedAuditorUsername(auditorUsername);
		driverOnboardingStepRepository.save(caseToAssign.get());

		return ResponseEntity.ok("You have been assigned this case" + caseToAssign.get().getId());
	}

	@GetMapping("/list")
	public ResponseEntity<DriverOnboardingStep> getAssignedAuditCase() {
		Optional<DriverOnboardingStep> assignedDriverOnboardingStep = driverOnboardingStepRepository.findByAssignedAuditorUsernameAndStatus(
			auditorUsername, StepStatus.Processing);
		if (assignedDriverOnboardingStep.isEmpty()) {
			//TODO Exception advice for no case assigned.
			throw new RuntimeException();
		}
		return ResponseEntity.ok(assignedDriverOnboardingStep.get());
	}

	@GetMapping(value = "/document/{documentName}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<Resource> getAssignedCaseDocument(
		@PathVariable("documentName") String requestedDocumentName) throws NoSuchFileException {
		DriverOnboardingStep assignedDriverOnboardingStep = fetchAssignedAuditCase(auditorUsername);
		Map<String, String> fileNameToS3Id = gson.fromJson(
			assignedDriverOnboardingStep.getAttachments(), Map.class);
		if (fileNameToS3Id.containsKey(requestedDocumentName)) {
			Optional<BudgetEditionS3> requestedDocument = budgetEditionS3Repository.findById(
				Integer.valueOf(fileNameToS3Id.get(requestedDocumentName)));
			if (requestedDocument.isEmpty()) {
				throw new RuntimeException("Dangling file pointer. This shouldn't have happened.");
			}
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.body(new ByteArrayResource(requestedDocument.get().getFileContent()));
		}
		throw new NoSuchFileException(requestedDocumentName);
	}

	@GetMapping("/document/list")
	public ResponseEntity<Map<String, String>> getDocumentURLMap() {
		//TODO Implement and return Map<documentName, URL>
		return ResponseEntity.ok(new HashMap<>());
	}

	@PostMapping("/approve")
	@Transactional
	public ResponseEntity<String> updateDocumentVerificationCaseStatusToApproved() {
		DriverOnboardingStep assignedDriverOnboardingStep = fetchAssignedAuditCase(auditorUsername);
		assignedDriverOnboardingStep.setStatus(StepStatus.Completed);
		DriverOnboardingProcess driverOnboardingProcess = driverOnboardingProcessRepository.findById(
			assignedDriverOnboardingStep.getDriverOnboardingProcessId()).get();
		Integer currentStepNumber = driverOnboardingProcess.getCurrentStepNumber();
		driverOnboardingProcess.setCurrentStepNumber(currentStepNumber + 1);
		driverOnboardingProcessRepository.save(driverOnboardingProcess);
		driverOnboardingStepRepository.save(assignedDriverOnboardingStep);
		return ResponseEntity.ok("done");
	}

	@PostMapping("/reject")
	public ResponseEntity<String> updateDocumentVerificationCaseStatusToRejected() {
		DriverOnboardingStep assignedDriverOnboardingStep = fetchAssignedAuditCase(auditorUsername);
		assignedDriverOnboardingStep.setStatus(StepStatus.Aborted);
		driverOnboardingStepRepository.save(assignedDriverOnboardingStep);
		return ResponseEntity.ok("done");
	}

	private DriverOnboardingStep fetchAssignedAuditCase(String auditorUsername) {
		Optional<DriverOnboardingStep> assignedDriverOnboardingStep =
			driverOnboardingStepRepository.findByAssignedAuditorUsernameAndStatus(
				auditorUsername, StepStatus.Processing);
		if (assignedDriverOnboardingStep.isEmpty()) {
			throw new NoCaseAssignedException();
		}
		return assignedDriverOnboardingStep.get();
	}
}
