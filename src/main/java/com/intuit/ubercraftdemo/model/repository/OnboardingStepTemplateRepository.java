package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.OnboardingStepTemplate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingStepTemplateRepository extends
	CrudRepository<OnboardingStepTemplate, Integer> {

	List<OnboardingStepTemplate> findByProcessTemplateId(Integer processTemplateId);
}
