    package com.fixmycar.model;

    import com.fasterxml.jackson.annotation.JsonBackReference;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import java.util.ArrayList;
    import java.util.List;
    import jakarta.persistence.*;
    import lombok.*;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class Car {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String brand;
        private String model;
        private String vin;
        private int year;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "customer_id", nullable = false)
        @JsonIgnoreProperties({"cars", "serviceRequests"})
        private Customer customer;

        @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnoreProperties({"car", "customer", "serviceCenter"})
        private List<ServiceRequest> serviceRequests = new ArrayList<>();

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "car_service_center",
                joinColumns = @JoinColumn(name = "car_id"),
                inverseJoinColumns = @JoinColumn(name = "service_center_id")
        )
        @JsonIgnoreProperties({"cars", "serviceRequests"})
        private List<ServiceCenter> serviceCenters = new ArrayList<>();
    }