package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.AuditorOnboardingStep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditorOnboardingStepRepository extends CrudRepository<AuditorOnboardingStep, Integer> {

    List<AuditorOnboardingStep> findAllByUsername(String username);

}
