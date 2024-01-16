package com.intuit.ubercraftdemo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "uber.onboarding_step_template")
@Data
public class OnboardingStepTemplate {

	@Id
	@Column(name = "step_name", length = 20, nullable = false)
	private String stepName;

	@Column(name = "initial_status", length = 15, nullable = false)
	@Enumerated(EnumType.STRING)
	private InitialStatus initialStatus;

	@Column(name = "attachments", columnDefinition = "json")
	private String attachments;

	@Id
	@Column(name = "parent_process_country", length = 10, nullable = false)
	private String parentProcessCountry;

	@Id
	@Column(name = "parent_process_state", length = 10, nullable = false)
	private String parentProcessState;

	@Id
	@Column(name = "parent_process_city", length = 10, nullable = false)
	private String parentProcessCity;

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "product_category", length = 10, nullable = false)
	private ProductCategory productCategory;

	@Column(name = "sequence_number")
	private Integer sequenceNumber;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "parent_process_country", referencedColumnName = "operating_market_country", insertable = false, updatable = false),
		@JoinColumn(name = "parent_process_state", referencedColumnName = "operating_market_state", insertable = false, updatable = false),
		@JoinColumn(name = "parent_process_city", referencedColumnName = "operating_market_city", insertable = false, updatable = false),
		@JoinColumn(name = "product_category", referencedColumnName = "product_category", insertable = false, updatable = false)
	})
	private OnboardingProcessTemplate onboardingProcessTemplate;

	// Getters and setters omitted for brevity

	public enum InitialStatus {
		NOT_STARTED, DRIVER_ACTION_NEEDED, WAITING_FOR_AUDITOR_ASSIGNMENT,
		PROCESSING, COMPLETED, ABORTED
	}

	public enum ProductCategory {
		UBERX, UBERGO, UBERPOOL
	}
}
