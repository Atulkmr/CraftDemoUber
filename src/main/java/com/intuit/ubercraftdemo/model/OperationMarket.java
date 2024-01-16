package com.intuit.ubercraftdemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "uber.operation_market")
@Data
public class OperationMarket {

	@Id
	@Column(name = "country", length = 10, nullable = false)
	private String country;

	@Id
	@Column(name = "state", length = 10, nullable = false)
	private String state;

	@Id
	@Column(name = "city", length = 10, nullable = false)
	private String city;

}