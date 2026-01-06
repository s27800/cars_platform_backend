package com.carsplatform.backend.api.outsideDimensions;

import com.carsplatform.backend.api.cars.Car;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outside_dimensions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutsideDimensions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer length;
    private Integer height;
    private Integer width;

    @Column(name = "width_with_mirrors")
    private Integer widthWithMirrors;

    @Column(name = "height_with_open_trunk")
    private Integer heightWithOpenTrunk;

    @Column(name = "wheel_base")
    private Integer wheelBase;

    @Column(name = "wheel_base_front")
    private Integer wheelBaseFront;

    @Column(name = "wheel_base_back")
    private Integer wheelBaseBack;

    @Column(name = "overhang_front")
    private Integer overhangFront;

    @Column(name = "overhang_back")
    private Integer overhangBack;

    private Integer clearance;

    @Column(name = "max_roof_load")
    private Integer maxRoofLoad;

    @OneToOne(mappedBy = "outsideDimensions")
    private Car car;
}
