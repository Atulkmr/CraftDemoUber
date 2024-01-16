package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "uber.onboarding_process_template")
@Data
public class OnboardingProcessTemplate {

	@Id
	private Integer id;
	private ProductCategory productCategory;
	private String processName;
	private Integer operationMarketId;
}