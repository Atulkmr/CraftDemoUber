package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "uber.onboarding_step_template")
@Data
public class OnboardingStepTemplate {

	@Id
	private Integer id;
	private String stepName;
	private String parentProcessCountry;
	private String parentProcessState;
	private String parentProcessCity;
	private ProductCategory productCategory;
	private StepStatus initialStatus;
	private String attachments;
	private Integer sequenceNumber;
	private OnboardingProcessTemplate onboardingProcessTemplate;
}
