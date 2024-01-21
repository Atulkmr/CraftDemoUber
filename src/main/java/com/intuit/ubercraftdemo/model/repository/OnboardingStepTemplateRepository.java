package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.OnboardingStepTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OnboardingStepTemplateRepository extends
        CrudRepository<OnboardingStepTemplate, Integer> {

    List<OnboardingStepTemplate> findByProcessTemplateId(Integer processTemplateId);
}
