package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.AuditorOnboardingStep;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditorOnboardingStepRepository extends CrudRepository<AuditorOnboardingStep, Integer> {

	List<AuditorOnboardingStep> findAllByUsername(String username);

}
