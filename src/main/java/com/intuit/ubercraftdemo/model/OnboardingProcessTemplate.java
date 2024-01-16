package com.intuit.ubercraftdemo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "uber.onboarding_process_template")
@Data
public class OnboardingProcessTemplate {

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

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "operating_market_country", referencedColumnName = "country", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_state", referencedColumnName = "state", insertable = false, updatable = false),
		@JoinColumn(name = "operating_market_city", referencedColumnName = "city", insertable = false, updatable = false)
	})
	private OperationMarket operationMarket;


	public enum ProductCategory {
		UBERX, UBERGO, UBERPOOL;
	}
}