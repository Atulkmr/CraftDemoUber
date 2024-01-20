package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.Driver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends CrudRepository<Driver, Integer> {

}
