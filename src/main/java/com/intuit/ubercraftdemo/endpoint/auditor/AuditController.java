package com.intuit.ubercraftdemo.endpoint.auditor;

import com.intuit.ubercraftdemo.AuditorOnboardingStepRepository;
import com.intuit.ubercraftdemo.DriverOnboardingStepRepository;
import com.intuit.ubercraftdemo.model.AuditorOnboardingStep;
import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController("/auditor/case")
public class AuditController {

	private DriverOnboardingStepRepository driverOnboardingStepRepository;
	private AuditorOnboardingStepRepository auditorOnboardingStepRepository;

	@Transactional
	@PatchMapping("/assign")
	public ResponseEntity<String> findAndAssignOldestAuditCase() {
		String auditorUsername = "kyc-auditor@gmail.com";
		List<AuditorOnboardingStep> auditorOnboardingStepsForThisUser = auditorOnboardingStepRepository.findAllByUsername(
			auditorUsername);
		Optional<DriverOnboardingStep> caseToAssign = driverOnboardingStepRepository.findOldestDriverOnboardingStepWaitingForAssignment(
			auditorOnboardingStepsForThisUser.stream()
				.map(AuditorOnboardingStep::getAssignedOnboardingStepId).collect(
					Collectors.toList()), StepStatus.WaitingForAuditorAssignment);
		if(caseToAssign.isEmpty()) {
			return ResponseEntity.ok("Nothing left to do.");
		}
		caseToAssign.get().setStatus(StepStatus.Processing);
		caseToAssign.get().setAssignedAuditorUsername(auditorUsername);
		driverOnboardingStepRepository.save(caseToAssign.get());

		return ResponseEntity.ok("You have been assigned this case" + caseToAssign.get().getId());
	}

	@GetMapping("/list")
	public ResponseEntity<String> getAssignedAuditCase() {
		return ResponseEntity.ok("");
	}
}
