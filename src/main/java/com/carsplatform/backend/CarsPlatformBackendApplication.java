package com.carsplatform.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Entry point of the Cars Platform REST API.
 *
 * Features live in their own packages under {@code api}. The ones the frontend calls directly
 * bring their own controller, service and repository; the packages describing parts of a car
 * ({@code engines}, {@code chassis}, {@code performances}, ...) hold only the entity, its
 * mapper and its DTOs, because they are always reached through a car. Security, error handling
 * and configuration sit in {@code common}.
 */
@SpringBootApplication
public class CarsPlatformBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarsPlatformBackendApplication.class, args);
    }

}
