package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.OperationMarket;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperationMarketRepository extends CrudRepository<OperationMarket, Integer> {

    @Query("SELECT id FROM operation_market where country = :country AND state = :state AND city = :city")
    Optional<OperationMarket> findByCountryStateCity(String country, String state, String city);
}
