package com.intuit.ubercraftdemo.endpoint.driver;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationAcknowledgementDTO {
	private String username;
	private String operatingCountry;
	private String operatingState;
	private String operatingCity;
	private final String message = "Congratulations! Your Uber journey has begun.";
}
