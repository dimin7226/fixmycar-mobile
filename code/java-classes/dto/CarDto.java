package com.fixmycar.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarDto {
    String brand;
    String model;
    String vin;
    int year;
}