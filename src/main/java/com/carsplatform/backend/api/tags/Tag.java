package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.cars.Car;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Tag name cannot be blank")
    @Size(max = 100)
    private String name;

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Car> cars = new HashSet<>();
}
