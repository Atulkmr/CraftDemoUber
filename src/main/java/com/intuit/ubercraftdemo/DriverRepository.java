package com.intuit.ubercraftdemo;

import com.intuit.ubercraftdemo.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {

}
