package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.Vehicle;
import com.intuit.ubercraftdemo.model.Vehicle.VehicleColour;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends CrudRepository<Vehicle, Integer> {

	@Query("SELECT id, occupancy FROM Vehicle where make = :make AND model = :model AND year = :year AND colour = :colour")
	Optional<Vehicle> findByMakeModelYearColour(String make, String model, Integer year,
		VehicleColour colour);

}