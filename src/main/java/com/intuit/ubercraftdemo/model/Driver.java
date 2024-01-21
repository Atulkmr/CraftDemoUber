package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Table(name = "driver")
public class Driver {

    @Id
    private Integer id;
    private String username;
    private String name;
    private DriverStatus status;
    private BloodGroup bloodGroup;
    private LocalDate birthdate;
    private Integer operationMarketId;
    private String mailingStreet;
    private String mailingCity;
    private String mailingState;
    private String mailingPostalCode;
    private String mailingCountry;
    private Integer vehicleId;
    private String vehicleRegistrationNumber;
    private String drivingLicenseNumber;
    private LocalDate drivingLicenseExpiryDate;
    private String drivingLicenseIssuingAuthority;

    public enum BloodGroup {
        A_POSITIVE, B_POSITIVE, AB_POSITIVE, AB_NEGATIVE, A_NEGATIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE
    }

    public enum DriverStatus {
        ONBOARDING, READY_TO_WORK, WORKING
    }
}
