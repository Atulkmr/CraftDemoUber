package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverOnboardingStepRepository extends
	CrudRepository<DriverOnboardingStep, Integer> {

}
