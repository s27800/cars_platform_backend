package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.carImages.CarImage;
import com.carsplatform.backend.api.chassis.Chassis;
import com.carsplatform.backend.api.dataProposal.DataProposal;
import com.carsplatform.backend.api.engines.Engine;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.insideDimensions.InsideDimensions;
import com.carsplatform.backend.api.outsideDimensions.OutsideDimensions;
import com.carsplatform.backend.api.performances.Performance;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.tags.Tag;
import com.carsplatform.backend.api.transmissions.Transmission;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * A single car variant in the catalogue, for example "BMW 320i 2020 Sedan".
 *
 * Following the database schema, the technical data sits in separate entities behind
 * one-to-one relations: engine, chassis, transmission, performance and the two sets of
 * dimensions. Collections pointing back to this entity are left out of toString, equals and
 * hashCode, otherwise Lombok walks the whole object graph.
 */
@Entity
@Table(name = "car")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Car name cannot be blank")
    @Size(max = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "doors_number", nullable = false)
    @NotNull(message = "Number of doors is required")
    private Integer doorsNumber;

    @Column(name = "seats_number", nullable = false)
    @NotNull(message = "Number of seats is required")
    private Integer seatsNumber;

    @Column(name = "production_years", nullable = false)
    @NotBlank(message = "Production years cannot be blank")
    @Size(max = 20)
    private String productionYears;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "body_type_id", nullable = false)
    private BodyType bodyType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "generation_id", nullable = false)
    private Generation generation;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "engine_id", nullable = false)
    private Engine engine;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chassis_id", nullable = false)
    private Chassis chassis;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "transmission_id", nullable = false)
    private Transmission transmission;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inside_dimensions_id", nullable = false)
    private InsideDimensions insideDimensions;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "outside_dimensions_id", nullable = false)
    private OutsideDimensions outsideDimensions;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<CarImage> images = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
            name = "car_tag",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private List<FuelReport> fuelReports = new ArrayList<>();

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private List<DataProposal> proposals = new ArrayList<>();
}
