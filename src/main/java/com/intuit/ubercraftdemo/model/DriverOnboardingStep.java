package com.intuit.ubercraftdemo.model;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "uber.driver_onboarding_step")
public class DriverOnboardingStep {

	@Id
	private Integer id;
	private String stepName;
	private Integer driverId;
	private ProductCategory productCategory;
	private Integer operatingMarketId;
	private Integer sequenceNumber;
	private StepStatus status;
	private String attachments;
	private Date createdDate;
	private Date lastModifiedDate;
	private Integer driverOnboardingProcessId;
	private Integer onboardingStepTemplateId;

}
