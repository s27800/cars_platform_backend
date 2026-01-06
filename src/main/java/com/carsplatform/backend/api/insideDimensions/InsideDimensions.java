package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.cars.Car;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inside_dimensions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsideDimensions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "height_from_seat_to_roof_front")
    private Integer heightFromSeatToRoofFront;

    @Column(name = "height_from_seat_to_roof_back")
    private Integer heightFromSeatToRoofBack;

    @Column(name = "max_trunk_space")
    private Integer maxTrunkSpace;

    @Column(name = "min_trunk_space")
    private Integer minTrunkSpace;

    @Column(name = "min_trunk_length")
    private Integer minTrunkLength;

    @Column(name = "max_trunk_length")
    private Integer maxTrunkLength;

    @Column(name = "trunk_width")
    private Integer trunkWidth;

    @Column(name = "trunk_height")
    private Integer trunkHeight;

    @OneToOne(mappedBy = "insideDimensions")
    private Car car;
}
