package com.carsplatform.backend.api.chassis;

import com.carsplatform.backend.api.cars.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "chassis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chassis {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "basic_rims")
    @Size(max = 50)
    private String basicRims;

    @Column(name = "optional_rims")
    @Size(max = 100)
    private String optionalRims;

    @Column(name = "basic_tires")
    @Size(max = 100)
    private String basicTires;

    @Column(name = "optional_tires")
    @Size(max = 100)
    private String optionalTires;

    @Column(name = "front_brakes")
    @Size(max = 100)
    private String frontBrakes;

    @Column(name = "back_brakes")
    @Size(max = 100)
    private String backBrakes;

    @Column(name = "front_brakes_radius")
    private Integer frontBrakesRadius;

    @Column(name = "back_brakes_radius")
    private Integer backBrakesRadius;

    @Column(name = "front_brakes_thickness")
    private Integer frontBrakesThickness;

    @Column(name = "back_brakes_thickness")
    private Integer backBrakesThickness;

    @Size(max = 100)
    private String suspension;

    @Size(max = 100)
    private String drive;

    @OneToOne(mappedBy = "chassis")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Car car;
}
