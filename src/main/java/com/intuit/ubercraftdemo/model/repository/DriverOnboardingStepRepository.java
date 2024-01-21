package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverOnboardingStepRepository extends
        CrudRepository<DriverOnboardingStep, Integer> {

    List<DriverOnboardingStep> findAllByDriverId(Integer driverId);

    Optional<DriverOnboardingStep> findByAssignedAuditorUsernameAndStatus(String username, StepStatus status);

    @Query("SELECT * FROM driver_onboarding_step WHERE onboarding_step_template_id = :onboardingStepTemplateId AND status = :status ORDER BY created_date LIMIT 1 FOR UPDATE SKIP LOCKED")
    Optional<DriverOnboardingStep> findOldestDriverOnboardingStep(List<Integer> onboardingStepTemplateId, StepStatus status);

}
