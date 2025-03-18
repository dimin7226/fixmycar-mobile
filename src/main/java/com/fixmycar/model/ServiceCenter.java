package com.fixmycar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import jakarta.persistence.*;
//import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String phone;

    @ManyToMany(mappedBy = "serviceCenters")
    @JsonIgnore
    private List<Car> cars = new ArrayList<>();

    @OneToMany(mappedBy = "serviceCenter", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ServiceRequest> serviceRequests = new ArrayList<>();
}