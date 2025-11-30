package com.carsplatform.backend.api.cars;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICarRepository extends JpaRepository<Car, Integer> {
    @Query("SELECT c FROM Car c " +
            "LEFT JOIN FETCH c.engine " +
            "LEFT JOIN FETCH c.chassis " +
            "LEFT JOIN FETCH c.transmission " +
            "LEFT JOIN FETCH c.performance " +
            "LEFT JOIN FETCH c.insideDimensions " +
            "LEFT JOIN FETCH c.outsideDimensions " +
            "LEFT JOIN FETCH c.generation " +
            "LEFT JOIN FETCH c.generation.model " +
            "LEFT JOIN FETCH c.generation.model.brand " +
            "LEFT JOIN FETCH c.bodyType " +
            "WHERE c.id = :id")
    Optional<Car> findByIdWithDetails(@Param("id") Integer id);
}
