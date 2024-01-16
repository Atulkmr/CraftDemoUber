package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "uber.driver_onboarding_process")
public class DriverOnboardingProcess {

	@Id
	private Integer id;
	private String driver;
	private String operatingMarketCountry;
	private String operatingMarketState;
	private String operatingMarketCity;
	private ProductCategory productCategory;
	private String processName;
	private Integer currentStepNumber;
	private OnboardingProcessTemplate onboardingProcessTemplate;
}
