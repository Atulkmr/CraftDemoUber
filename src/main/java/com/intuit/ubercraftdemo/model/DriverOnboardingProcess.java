package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "driver_onboarding_process")
public class DriverOnboardingProcess {

    @Id
    private Integer id;
    private Integer driverId;
    private Integer productCategoryId;
    private String processName;
    private Integer currentStepNumber;
    private Integer onboardingProcessTemplateId;
}
