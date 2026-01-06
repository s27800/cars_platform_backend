package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.models.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "brand")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Brand name cannot be blank")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String name;

    @Column(name = "country", nullable = false)
    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100)
    private String country;

    @Column(name = "founded_year")
    private Integer foundedYear;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Model> models = new ArrayList<>();
}
