package com.carsplatform.backend.api.performances;

import com.carsplatform.backend.api.cars.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "performance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "max_speed")
    private Integer maxSpeed;

    @Column(name = "acceleration_0_100", precision = 4, scale = 2)
    @Digits(integer = 2, fraction = 2)
    private BigDecimal acceleration0100;

    @Column(name = "acceleration_100_200", precision = 5, scale = 2)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal acceleration100200;

    @Column(name = "fuel_tank_capacity")
    private Integer fuelTankCapacity;

    @Column(name = "fuel_consumption_city", precision = 3, scale = 1)
    @Digits(integer = 2, fraction = 1)
    private BigDecimal fuelConsumptionCity;

    @Column(name = "fuel_consumption_route", precision = 3, scale = 1)
    @Digits(integer = 2, fraction = 1)
    private BigDecimal fuelConsumptionRoute;

    @Column(name = "fuel_consumption_mixed", precision = 3, scale = 1)
    @Digits(integer = 2, fraction = 1)
    private BigDecimal fuelConsumptionMixed;

    @Column(name = "range_city")
    private Integer rangeCity;

    @Column(name = "range_route")
    private Integer rangeRoute;

    @Column(name = "range_mixed")
    private Integer rangeMixed;

    @Column(name = "emission_co2")
    private Integer emissionCo2;

    @Column(name = "fuel_emission_norm")
    @Size(max = 50)
    private String fuelEmissionNorm;

    @OneToOne(mappedBy = "performance")
    @JsonIgnore
    private Car car;
}
