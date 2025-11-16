package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.cars.Car;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transmission")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transmission_type")
    @Size(max = 100)
    private String transmissionType;

    @Column(name = "transmission_name")
    @Size(max = 100)
    private String transmissionName;

    @Column(name = "gears_number")
    private Integer gearsNumber;

    @Column(name = "clutch_type")
    @Size(max = 50)
    private String clutchType;

    @OneToOne(mappedBy = "transmission", cascade = CascadeType.ALL)
    private Car car;
}
