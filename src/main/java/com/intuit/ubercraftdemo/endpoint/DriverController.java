package com.intuit.ubercraftdemo.endpoint;

import com.intuit.ubercraftdemo.Driver;
import com.intuit.ubercraftdemo.DriverMapper;
import com.intuit.ubercraftdemo.DriverRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class DriverController {

	private DriverRepository repository;
	private DriverMapper driverMapper;

	@PostMapping("/driver/register")
	public ResponseEntity<Driver> saveEntity(@RequestBody DriverDTO driverRegistration) {
		Driver entity = driverMapper.toEntity(driverRegistration);
		Driver driver = repository.save(entity);
		return ResponseEntity.ok(driver);
	}
}
