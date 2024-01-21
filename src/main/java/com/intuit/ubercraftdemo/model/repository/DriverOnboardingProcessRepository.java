package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.DriverOnboardingProcess;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverOnboardingProcessRepository extends
        CrudRepository<DriverOnboardingProcess, Integer> {

    Optional<DriverOnboardingProcess> findByDriverId(Integer driverId);
}
