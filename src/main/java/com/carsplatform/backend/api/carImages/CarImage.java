package com.carsplatform.backend.api.carImages;

import com.carsplatform.backend.api.cars.Car;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "car_image")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "image_url", nullable = false)
    @NotBlank(message = "Image URL cannot be blank")
    @Size(max = 255)
    private String imageUrl;

    @Column(name = "is_main")
    private Boolean isMain = false;
}
