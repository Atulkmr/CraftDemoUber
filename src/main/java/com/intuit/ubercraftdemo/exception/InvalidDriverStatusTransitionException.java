package com.intuit.ubercraftdemo.exception;

import com.intuit.ubercraftdemo.model.Driver.DriverStatus;

public class InvalidDriverStatusTransitionException extends RuntimeException {
	public InvalidDriverStatusTransitionException() {
		super(String.format("Can't toggle the status when the current status is %s", DriverStatus.ONBOARDING));
	}
}
