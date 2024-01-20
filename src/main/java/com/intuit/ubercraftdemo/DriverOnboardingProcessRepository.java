package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverOnboardingProcessRepository extends
	CrudRepository<DriverOnboardingProcess, Integer> {

	Optional<DriverOnboardingProcess> findByDriverId(Integer driverId);
}
