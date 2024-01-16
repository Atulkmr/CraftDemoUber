package com.intuit.ubercraftdemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;

@Entity
@Data
@Table(name = "uber.driver_onboarding_step")
public class DriverOnboardingStep {

	@Id
	@Column(name = "step_name", length = 15, nullable = false)
	private String stepName;

	@Id
	@Column(name = "sequence_number", nullable = false)
	private Integer sequenceNumber;

	@Id
	@Column(name = "driver", length = 20, nullable = false)
	private String driver;

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "product_category", length = 10, nullable = false)
	private ProductCategory productCategory;

	@Id
	@Column(name = "operating_market_country", length = 10, nullable = false)
	private String operatingMarketCountry;

	@Id
	@Column(name = "operating_market_state", length = 10, nullable = false)
	private String operatingMarketState;

	@Id
	@Column(name = "operating_market_city", length = 10, nullable = false)
	private String operatingMarketCity;

	@Column(name = "status", length = 15, nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "attachments", columnDefinition = "json")
	private String attachments;

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.DATE)

	private Date createdDate;

	@Column(name = "last_modified_date")

	@Temporal(TemporalType.DATE)

	private Date lastModifiedDate;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "operating_market_country", referencedColumnName = "operating_market_country", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_state", referencedColumnName = "operating_market_state", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_city", referencedColumnName = "operating_market_city", insertable = false, updatable = false),
		@JoinColumn(name = "product_category", referencedColumnName = "product_category", insertable = false, updatable = false),
		@JoinColumn(name = "driver", referencedColumnName = "driver", insertable = false, updatable = false)
	})
	private DriverOnboardingProcess driverOnboardingProcess;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "operating_market_country", referencedColumnName = "parent_process_country", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_state", referencedColumnName = "parent_process_state", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_city", referencedColumnName = "parent_process_city", insertable = false, updatable = false),
		@JoinColumn(name = "product_category", referencedColumnName = "product_category", insertable = false, updatable = false),
		@JoinColumn(name = "step_name", referencedColumnName = "step_name", insertable = false, updatable = false)
	})
	private OnboardingStepTemplate onboardingStepTemplate;

	public enum ProductCategory {
		UBERX, UBERGO, UBERPOOL
	}

	public enum Status {
		NOT_STARTED, DRIVER_ACTION_NEEDED, WAITING_FOR_AUDITOR_ASSIGNMENT, PROCESSING, COMPLETED, ABORTED
	}
}
