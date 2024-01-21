package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table(name = "onboarding_process_template")
public class OnboardingProcessTemplate {

    @Id
    private Integer id;
    private Integer productCategoryId;
    private String processName;
    private Integer operationMarketId;
}