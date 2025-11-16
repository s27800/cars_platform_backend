package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.models.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Generation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Generation name cannot be blank")
    @Size(max = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @OneToMany(mappedBy = "generation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars = new ArrayList<>();
}
