package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.OnboardingProcessTemplate;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnboardingProcessTemplateRepository extends
	CrudRepository<OnboardingProcessTemplate, Integer> {

	@Query("SELECT id, process_name FROM onboarding_process_template where operating_market_id = :operatingMarketId AND product_category_id = :productCategoryId")
	Optional<OnboardingProcessTemplate> findByOperationMarketIdAndProductCategory(
		Integer operatingMarketId, Integer productCategoryId);

}
