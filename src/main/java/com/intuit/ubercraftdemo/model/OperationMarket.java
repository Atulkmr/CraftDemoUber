package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "uber.operation_market")
@Data
public class OperationMarket {

	@Id
	private Integer id;
	private String country;
	private String state;
	private String city;

}