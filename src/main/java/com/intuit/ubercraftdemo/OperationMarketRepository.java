package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.OperationMarket;
import java.util.Optional;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationMarketRepository extends CrudRepository<OperationMarket, Integer> {

	@Query("SELECT id FROM operation_market where country = :country AND state = :state AND city = :city")
	Optional<OperationMarket> findByCountryStateCity(String country, String state, String city);
}
