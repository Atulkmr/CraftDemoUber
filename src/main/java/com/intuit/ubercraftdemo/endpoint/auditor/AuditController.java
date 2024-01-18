package com.intuit.ubercraftdemo.endpoint.auditor;

import com.google.gson.Gson;
import com.intuit.ubercraftdemo.AuditorOnboardingStepRepository;
import com.intuit.ubercraftdemo.BudgetEditionS3Repository;
import com.intuit.ubercraftdemo.DriverOnboardingStepRepository;
import com.intuit.ubercraftdemo.model.AuditorOnboardingStep;
import com.intuit.ubercraftdemo.model.BudgetEditionS3;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/auditor/case")
public class AuditController {

	//TODO the auditor username is hardcoded right now. Ideally it should be obtained from the auth token.
	private final static String auditorUsername = "kyc-auditor@gmail.com";
	private final DriverOnboardingStepRepository driverOnboardingStepRepository;
	private final AuditorOnboardingStepRepository auditorOnboardingStepRepository;
	private final BudgetEditionS3Repository budgetEditionS3Repository;
	private final Gson gson;

	@Transactional
	@PatchMapping("/assign")
	public ResponseEntity<String> findAndAssignOldestAuditCase() {
		List<AuditorOnboardingStep> auditorOnboardingStepsForThisUser = auditorOnboardingStepRepository.findAllByUsername(
			auditorUsername);
		Optional<DriverOnboardingStep> caseToAssign = driverOnboardingStepRepository.findOldestDriverOnboardingStepWaitingForAssignment(
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
		Optional<DriverOnboardingStep> assignedDriverOnboardingStep = driverOnboardingStepRepository.findByStatusAndAssignedAuditorUsername(
			StepStatus.Processing, auditorUsername);
		if (assignedDriverOnboardingStep.isEmpty()) {
			//TODO Exception advice for no case assigned.
			throw new RuntimeException();
		}
		return ResponseEntity.ok(assignedDriverOnboardingStep.get());
	}

	@GetMapping(value = "/document/{documentName}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<Resource> getAssignedCaseDocument(@PathVariable("documentName") String requestedDocumentName) {
		Optional<DriverOnboardingStep> assignedDriverOnboardingStep = driverOnboardingStepRepository.findByStatusAndAssignedAuditorUsername(
			StepStatus.Processing, auditorUsername);
		if (assignedDriverOnboardingStep.isEmpty()) {
			//TODO Exception advice for no case assigned.
			throw new RuntimeException();
		}
		Map<String, String> fileNameToS3Id = gson.fromJson(
			assignedDriverOnboardingStep.get().getAttachments(), Map.class);
		if (fileNameToS3Id.containsKey(requestedDocumentName)) {
			Optional<BudgetEditionS3> requestedDocument = budgetEditionS3Repository.findById(
				Integer.valueOf(fileNameToS3Id.get(requestedDocumentName)));
			if (requestedDocument.isEmpty()) {
				throw new RuntimeException("Dangling file pointer. This shouldn't have happened.");
			}
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.body(new ByteArrayResource(requestedDocument.get().getFileContent()));
		}
		//TODO Exception advice for no such file found.
		throw new RuntimeException();
	}

	public ResponseEntity<String> updateAssignedAuditCaseStatusToApproved() {
		return null;
	}

	public ResponseEntity<String> updateAssignedAuditCaseStatusToRejected() {
		return null;
	}
}
