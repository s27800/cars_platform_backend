package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.cars.Car;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "body_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Body type name cannot be blank")
    @Size(max = 50)
    private String name;

    @OneToMany(mappedBy = "bodyType")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Car> cars = new ArrayList<>();
}
