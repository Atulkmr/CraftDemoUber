package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "auditor_onboarding_step")
public class AuditorOnboardingStep {

	@Id
	private Integer id;
	private String username;
	private Integer assignedOnboardingStepId;
	private String stepName;
}
