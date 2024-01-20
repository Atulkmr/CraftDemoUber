package com.intuit.ubercraftdemo.advice;

import com.intuit.ubercraftdemo.model.DriverOnboardingStep;
import com.intuit.ubercraftdemo.model.StepStatus;

/**
 * This exception is thrown when API corresponding to a currently inactive step is invoked.
 */
public class InvalidStepModificationException extends IllegalStateException {

	public InvalidStepModificationException(DriverOnboardingStep activeStep, DriverOnboardingStep inactiveStep) {
		super(String.format("%s step is currently disabled, current active step is %s",
			inactiveStep.getStepName(), activeStep.getStepName()));
	}

	public InvalidStepModificationException(StepStatus current, StepStatus needed) {
		super(String.format("Current status is %s, allowed status is %s", current, needed));
	}
}
