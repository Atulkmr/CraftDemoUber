package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.endpoint.driver.DriverRegistrationDTO;
import com.intuit.ubercraftdemo.endpoint.driver.DriverRegistrationDTO.VehicleDTO;
import com.intuit.ubercraftdemo.model.Driver;
import com.intuit.ubercraftdemo.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "drivingLicenseExpiryDate", source = "drivingLicense.expiryDate")
    @Mapping(target = "drivingLicenseNumber", source = "drivingLicense.number")
    @Mapping(target = "drivingLicenseIssuingAuthority", source = "drivingLicense.issuingAuthority")
    @Mapping(target = "vehicleRegistrationNumber", source = "vehicle.registrationNumber")
    @Mapping(target = "mailingStreet", source = "mailingAddress.street")
    @Mapping(target = "mailingCity", source = "mailingAddress.city")
    @Mapping(target = "mailingState", source = "mailingAddress.state")
    @Mapping(target = "mailingCountry", source = "mailingAddress.country")
    @Mapping(target = "mailingPostalCode", source = "mailingAddress.postalCode")
    Driver toEntity(DriverRegistrationDTO driverRegistrationDTO);

    Vehicle toEntity(VehicleDTO vehicleDTO);


    @Mapping(source = "drivingLicenseExpiryDate", target = "drivingLicense.expiryDate")
    @Mapping(source = "drivingLicenseNumber", target = "drivingLicense.number")
    @Mapping(source = "drivingLicenseIssuingAuthority", target = "drivingLicense.issuingAuthority")
    @Mapping(source = "vehicleRegistrationNumber", target = "vehicle.registrationNumber")
    @Mapping(source = "mailingStreet", target = "mailingAddress.street")
    @Mapping(source = "mailingCity", target = "mailingAddress.city")
    @Mapping(source = "mailingState", target = "mailingAddress.state")
    @Mapping(source = "mailingCountry", target = "mailingAddress.country")
    @Mapping(source = "mailingPostalCode", target = "mailingAddress.postalCode")
    DriverRegistrationDTO toDto(Driver driver);
}