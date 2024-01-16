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
import lombok.Data;

@Entity
@Table(name = "uber.driver_onboarding_process")
@Data
public class DriverOnboardingProcess {

	@Id
	@Column(name = "driver", length = 20, nullable = false)
	private String driver;

	@Id
	@Column(name = "operating_market_country", length = 10, nullable = false)
	private String operatingMarketCountry;

	@Id
	@Column(name = "operating_market_state", length = 10, nullable = false)
	private String operatingMarketState;

	@Id
	@Column(name = "operating_market_city", length = 10, nullable = false)
	private String operatingMarketCity;

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "product_category", length = 10, nullable = false)
	private ProductCategory productCategory;

	@Column(name = "process_name", length = 20)
	private String processName;

	@Column(name = "current_step_number")
	private Integer currentStepNumber;

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "operating_market_country", referencedColumnName = "operating_market_country", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_state", referencedColumnName = "operating_market_state", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_city", referencedColumnName = "operating_market_city", insertable = false, updatable = false),
		@JoinColumn(name = "product_category", referencedColumnName = "product_category", insertable = false, updatable = false)
	})
	private OnboardingProcessTemplate onboardingProcessTemplate;

	public enum ProductCategory {
		UBERX, UBERGO, UBERPOOL
	}
}
