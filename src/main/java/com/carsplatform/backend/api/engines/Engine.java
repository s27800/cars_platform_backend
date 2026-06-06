package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.cars.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "engine")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Engine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "engine_code")
    @Size(max = 50)
    private String engineCode;

    @Column(name = "production_years")
    @Size(max = 20)
    private String productionYears;

    private Integer displacement;

    @Column(name = "engine_type")
    @Size(max = 50)
    private String engineType;

    @Column(name = "max_power")
    private Integer maxPower;

    @Column(name = "max_power_rotation_speed")
    private Integer maxPowerRotationSpeed;

    @Size(max = 100)
    private String turbo;

    @Column(name = "cylinders_number")
    private Integer cylindersNumber;

    @Column(name = "cylinders_layout")
    @Size(max = 50)
    private String cylindersLayout;

    @Column(name = "valves_number")
    private Integer valvesNumber;

    @Size(max = 50)
    private String ignition;

    @Column(name = "injection_type")
    @Size(max = 50)
    private String injectionType;

    @Column(name = "max_torque")
    private Integer maxTorque;

    @Column(name = "max_torque_rotation_speed")
    private Integer maxTorqueRotationSpeed;

    @OneToOne(mappedBy = "engine")
    @JsonIgnore
    private Car car;
}
