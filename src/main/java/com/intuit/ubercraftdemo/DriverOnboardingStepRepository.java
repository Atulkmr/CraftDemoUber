package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverOnboardingStepRepository extends
	CrudRepository<DriverOnboardingStep, Integer> {

	Optional<DriverOnboardingStep> findByDriverIdAndOnboardingStepTemplateId(
		Integer driverId, Integer onboardingStepTemplateId);

	Optional<DriverOnboardingStep> findByStatusAndAssignedAuditorUsername(StepStatus status, String username);

	@Query("SELECT * FROM driver_onboarding_step WHERE onboarding_step_template_id = :onboardingStepTemplateId AND status = :status ORDER BY created_date LIMIT 1 FOR UPDATE SKIP LOCKED")
	Optional<DriverOnboardingStep> findOldestDriverOnboardingStepWaitingForAssignment(List<Integer> onboardingStepTemplateId, StepStatus status);

}
