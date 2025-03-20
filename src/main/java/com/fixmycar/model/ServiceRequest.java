package com.fixmycar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private LocalDateTime createdAt;
    private String status;

    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonIgnoreProperties({"serviceRequests", "customer", "serviceCenters"})
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"serviceRequests", "cars"})
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "service_center_id")
    @JsonIgnoreProperties({"serviceRequests", "cars"})
    private ServiceCenter serviceCenter;
}