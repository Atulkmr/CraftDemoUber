package com.intuit.ubercraftdemo.model.repository;

import com.intuit.ubercraftdemo.model.BudgetEditionS3;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetEditionS3Repository extends CrudRepository<BudgetEditionS3, Integer> {

}
