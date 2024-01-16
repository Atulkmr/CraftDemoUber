package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.endpoint.DriverDTO;
import com.intuit.ubercraftdemo.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

	@Mapping(target = "drivingLicenseExpiryDate", source = "drivingLicense.expiryDate")
	@Mapping(target = "drivingLicenseNumber", source = "drivingLicense.number")
	@Mapping(target = "drivingLicenseIssuingAuthority", source = "drivingLicense.issuingAuthority")
	@Mapping(target = "vehicleMake", source = "vehicle.make")
	@Mapping(target = "vehicleModel", source = "vehicle.model")
	@Mapping(target = "vehicleYear", source = "vehicle.year")
	@Mapping(target = "vehicleColour", source = "vehicle.colour")
	@Mapping(target = "vehicleRegistrationNumber", source = "vehicle.registrationNumber")
	@Mapping(target = "mailingAddressStreet", source = "mailingAddress.street")
	@Mapping(target = "mailingAddressCity", source = "mailingAddress.city")
	@Mapping(target = "mailingAddressState", source = "mailingAddress.state")
	@Mapping(target = "mailingAddressCountry", source = "mailingAddress.country")
	@Mapping(target = "mailingAddressPostalCode", source = "mailingAddress.postalCode")
	Driver toEntity(DriverDTO driverDTO);


	@Mapping(source = "drivingLicenseExpiryDate", target = "drivingLicense.expiryDate")
	@Mapping(source = "drivingLicenseNumber", target = "drivingLicense.number")
	@Mapping(source = "drivingLicenseIssuingAuthority", target = "drivingLicense.issuingAuthority")
	@Mapping(source = "vehicleMake", target = "vehicle.make")
	@Mapping(source = "vehicleModel", target = "vehicle.model")
	@Mapping(source = "vehicleYear", target = "vehicle.year")
	@Mapping(source = "vehicleColour", target = "vehicle.colour")
	@Mapping(source = "vehicleRegistrationNumber", target = "vehicle.registrationNumber")
	@Mapping(source = "mailingAddressStreet", target = "mailingAddress.street")
	@Mapping(source = "mailingAddressCity", target = "mailingAddress.city")
	@Mapping(source = "mailingAddressState", target = "mailingAddress.state")
	@Mapping(source = "mailingAddressCountry", target = "mailingAddress.country")
	@Mapping(source = "mailingAddressPostalCode", target = "mailingAddress.postalCode")
	DriverDTO toDto(Driver driver);
}