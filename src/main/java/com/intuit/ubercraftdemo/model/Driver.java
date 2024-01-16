package com.intuit.ubercraftdemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "driver")
public class Driver {

	@Id
	@Column(name = "username", length = 20, nullable = false)
	private String username;

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "blood_group", length = 3)
	private BloodGroup bloodGroup;

	@Column(name = "birthdate")
	@Temporal(TemporalType.DATE)
	private LocalDate birthdate;

	@Column(name = "operating_country", length = 10, nullable = false)
	private String operatingCountry;

	@Column(name = "operating_state", length = 10, nullable = false)
	private String operatingState;

	@Column(name = "operating_city", length = 10, nullable = false)
	private String operatingCity;

	@Column(name = "mailing_street")
	private String mailingAddressStreet;

	@Column(name = "mailing_city", length = 50)
	private String mailingAddressCity;

	@Column(name = "mailing_state", length = 50)
	private String mailingAddressState;

	@Column(name = "mailing_postal_code", length = 10)
	private String mailingAddressPostalCode;

	@Column(name = "mailing_country", length = 50)
	private String mailingAddressCountry;

	@Column(name = "vehicle_make", nullable = false)
	private String vehicleMake;

	@Column(name = "vehicle_model", nullable = false)
	private String vehicleModel;

	@Column(name = "vehicle_year", nullable = false)
	private String vehicleYear;

	@Column(name = "vehicle_colour", nullable = false)
	private String vehicleColour;

	@Column(name = "vehicle_registration_number", length = 10, nullable = false)
	private String vehicleRegistrationNumber;

	@Column(name = "driving_license_number", length = 15, nullable = false)
	private String drivingLicenseNumber;

	@Column(name = "driving_license_expiry_date")
	@Temporal(TemporalType.DATE)
	private LocalDate drivingLicenseExpiryDate;

	@Column(name = "driving_license_issuing_authority")
	private String drivingLicenseIssuingAuthority;

	public enum BloodGroup {
		A_POSITIVE, B_POSITIVE, AB_POSITIVE, AB_NEGATIVE, A_NEGATIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE
	}
}
