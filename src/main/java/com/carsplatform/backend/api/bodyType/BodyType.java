package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.cars.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "body_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Body type name cannot be blank")
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "bodyType")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Car> cars = new ArrayList<>();
}
