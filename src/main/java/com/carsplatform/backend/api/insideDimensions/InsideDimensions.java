package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.cars.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "inside_dimensions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsideDimensions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

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
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Car car;
}
