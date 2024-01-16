package com.intuit.ubercraftdemo.endpoint;

import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.DriverRepository;
import com.intuit.ubercraftdemo.OperationMarketRepository;
import com.intuit.ubercraftdemo.VehicleRepository;
import com.intuit.ubercraftdemo.model.Driver;
import com.intuit.ubercraftdemo.model.OperationMarket;
import com.intuit.ubercraftdemo.model.Vehicle;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DriverController {

	private DriverRepository driverRepository;
	private VehicleRepository vehicleRepository;
	private OperationMarketRepository operationMarketRepository;
	private DriverMapper driverMapper;

	@PostMapping("/driver/register")
	public ResponseEntity<Driver> saveEntity(@RequestBody DriverDTO driverRegistration) {
		Optional<Vehicle> vehicle = vehicleRepository.findByMakeModelYearColour(
			driverRegistration.getVehicle().getMake(), driverRegistration.getVehicle().getModel(),
			driverRegistration.getVehicle().getYear(), driverRegistration.getVehicle().getColour());

		Optional<OperationMarket> operationMarket = operationMarketRepository.findByCountryStateCity(
			driverRegistration.getOperatingCountry(), driverRegistration.getOperatingState(),
			driverRegistration.getOperatingCity());

		Driver driver = driverMapper.toEntity(driverRegistration);
		driver.setVehicleId(vehicle.get().getId());
		driver.setOperationMarketId(operationMarket.get().getId());
		return ResponseEntity.ok(driverRepository.save(driver));
	}
}
