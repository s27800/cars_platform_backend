package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.models.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "generation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Generation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Generation name cannot be blank")
    @Size(max = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    @JsonIgnore
    private Model model;

    @OneToMany(mappedBy = "generation", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Car> cars = new ArrayList<>();
}
