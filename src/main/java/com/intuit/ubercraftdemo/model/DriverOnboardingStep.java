package com.intuit.ubercraftdemo.model;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "driver_onboarding_step")
public class DriverOnboardingStep {

	@Id
	private Integer id;
	private String stepName;
	private Integer driverId;
	//	private Integer operatingMarketId;
	private Integer stepNumber;
	private StepStatus status;
	//TODO Should this username field be changed to a FK to User / Auditor table once it's there?
	private String assignedAuditorUsername;
	private String attachments;
	private Date createdDate;
	private Date lastModifiedDate;
	private Integer driverOnboardingProcessId;
	private Integer onboardingStepTemplateId;

}
