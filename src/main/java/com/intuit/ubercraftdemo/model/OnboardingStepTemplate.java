package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "onboarding_step_template")
@Data
public class OnboardingStepTemplate {

	@Id
	private Integer id;
	private String stepName;
	private Integer processTemplateId;
	private StepStatus initialStatus;
	private String attachments;
	private Integer sequenceNumber;
}
