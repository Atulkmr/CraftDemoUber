package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverOnboardingProcessRepository extends
	CrudRepository<DriverOnboardingProcess, Integer> {

}
