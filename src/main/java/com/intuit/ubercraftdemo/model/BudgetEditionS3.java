package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "budget_edition_s3")
@Data
public class BudgetEditionS3 {

	@Id
	private Integer id;
	private byte[] fileContent;
}
