package com.intuit.ubercraftdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "vehicle")
public class Vehicle {

    @Id
    private Integer id;
    private String make;
    private String model;
    private Integer year;
    private VehicleColour colour;
    private Integer occupancy;
    private Integer defaultProductCategoryId;

    public enum VehicleColour {
        Red, White, Black, Grey, Blue
    }
}
