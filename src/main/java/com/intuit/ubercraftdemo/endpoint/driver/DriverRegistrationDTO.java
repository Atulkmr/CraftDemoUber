package com.intuit.ubercraftdemo.endpoint.driver;

import com.intuit.ubercraftdemo.model.Vehicle.VehicleColour;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class DriverRegistrationDTO {

    private String username;
    private String name;
    private BloodGroup bloodGroup;
    private LocalDate birthdate;
    private String operatingCountry;
    private String operatingState;
    private String operatingCity;
    private MailingAddressDTO mailingAddress;
    private VehicleDTO vehicle;
    private DrivingLicenseDTO drivingLicense;

    public enum BloodGroup {
        A_POSITIVE, B_POSITIVE, AB_POSITIVE, AB_NEGATIVE, A_NEGATIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE
    }

    @Data
    public static class MailingAddressDTO {

        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String country;
    }

    @Data
    public static class VehicleDTO {

        private String registrationNumber;
        private String make;
        private String model;
        private Integer year;
        private VehicleColour colour;
    }

    @Data
    public static class DrivingLicenseDTO {

        private String number;
        private LocalDate expiryDate;
        private String issuingAuthority;
    }
}
