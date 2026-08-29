package com.carsplatform.backend.api.carImages;

import com.carsplatform.backend.api.cars.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "car_image")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Car car;

    @Column(name = "image_url", nullable = false, length = 500)
    @NotBlank(message = "Image URL cannot be blank")
    @Size(max = 500)
    private String imageUrl;

    @Column(name = "is_main")
    private Boolean isMain = false;
}
