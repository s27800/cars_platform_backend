package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.Generation;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "model")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonIgnore
    private Brand brand;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Model name cannot be blank")
    @Size(max = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Builder.Default
    private List<Generation> generations = new ArrayList<>();
}
