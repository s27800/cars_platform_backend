package com.carsplatform.backend.api.cars;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query(value = "SELECT c FROM Car c " +
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
            "WHERE " +
            "(:brandIds IS NULL OR c.generation.model.brand.id IN :brandIds) AND " +
            "(:modelIds IS NULL OR c.generation.model.id IN :modelIds) AND " +
            "(:generationIds IS NULL OR c.generation.id IN :generationIds) AND " +
            "(:bodyTypeIds IS NULL OR c.bodyType.id IN :bodyTypeIds) AND " +
            "(:tagIds IS NULL OR EXISTS (SELECT t FROM c.tags t WHERE t.id IN :tagIds)) AND " +
            "(:minDisplacement IS NULL OR c.engine.displacement >= :minDisplacement) AND " +
            "(:maxDisplacement IS NULL OR c.engine.displacement <= :maxDisplacement) AND " +
            "(:engineTypes IS NULL OR c.engine.engineType IN :engineTypes) AND " +
            "(:minPower IS NULL OR c.engine.maxPower >= :minPower) AND " +
            "(:maxPower IS NULL OR c.engine.maxPower <= :maxPower) AND " +
            "(:minTorque IS NULL OR c.engine.maxTorque >= :minTorque) AND " +
            "(:maxTorque IS NULL OR c.engine.maxTorque <= :maxTorque) AND " +
            "(:drives IS NULL OR c.chassis.drive IN :drives) AND " +
            "(:transmissionTypes IS NULL OR c.transmission.transmissionType IN :transmissionTypes) AND " +
            "(:minSpeed IS NULL OR c.performance.maxSpeed >= :minSpeed) AND " +
            "(:maxSpeed IS NULL OR c.performance.maxSpeed <= :maxSpeed) AND " +
            "(:minFuelConsumptionMixed IS NULL OR c.performance.fuelConsumptionMixed >= :minFuelConsumptionMixed) AND " +
            "(:maxFuelConsumptionMixed IS NULL OR c.performance.fuelConsumptionMixed <= :maxFuelConsumptionMixed)",

            countQuery = "SELECT count(c) FROM Car c " +
            "WHERE " +
            "(:brandIds IS NULL OR c.generation.model.brand.id IN :brandIds) AND " +
            "(:modelIds IS NULL OR c.generation.model.id IN :modelIds) AND " +
            "(:generationIds IS NULL OR c.generation.id IN :generationIds) AND " +
            "(:bodyTypeIds IS NULL OR c.bodyType.id IN :bodyTypeIds) AND " +
            "(:tagIds IS NULL OR EXISTS (SELECT t FROM c.tags t WHERE t.id IN :tagIds)) AND " +
            "(:minDisplacement IS NULL OR c.engine.displacement >= :minDisplacement) AND " +
            "(:maxDisplacement IS NULL OR c.engine.displacement <= :maxDisplacement) AND " +
            "(:engineTypes IS NULL OR c.engine.engineType IN :engineTypes) AND " +
            "(:minPower IS NULL OR c.engine.maxPower >= :minPower) AND " +
            "(:maxPower IS NULL OR c.engine.maxPower <= :maxPower) AND " +
            "(:minTorque IS NULL OR c.engine.maxTorque >= :minTorque) AND " +
            "(:maxTorque IS NULL OR c.engine.maxTorque <= :maxTorque) AND " +
            "(:drives IS NULL OR c.chassis.drive IN :drives) AND " +
            "(:transmissionTypes IS NULL OR c.transmission.transmissionType IN :transmissionTypes) AND " +
            "(:minSpeed IS NULL OR c.performance.maxSpeed >= :minSpeed) AND " +
            "(:maxSpeed IS NULL OR c.performance.maxSpeed <= :maxSpeed) AND " +
            "(:minFuelConsumptionMixed IS NULL OR c.performance.fuelConsumptionMixed >= :minFuelConsumptionMixed) AND " +
            "(:maxFuelConsumptionMixed IS NULL OR c.performance.fuelConsumptionMixed <= :maxFuelConsumptionMixed)")
    Page<Car> searchCars(
            @Param("brandIds") List<Integer> brandIds,
            @Param("modelIds") List<Integer> modelIds,
            @Param("generationIds") List<Integer> generationIds,
            @Param("bodyTypeIds") List<Integer> bodyTypeIds,
            @Param("tagIds") List<Integer> tagIds,
            @Param("minDisplacement") Integer minDisplacement,
            @Param("maxDisplacement") Integer maxDisplacement,
            @Param("engineTypes") List<String> engineTypes,
            @Param("minPower") Integer minPower,
            @Param("maxPower") Integer maxPower,
            @Param("minTorque") Integer minTorque,
            @Param("maxTorque") Integer maxTorque,
            @Param("drives") List<String> drives,
            @Param("transmissionTypes") List<String> transmissionTypes,
            @Param("minSpeed") Integer minSpeed,
            @Param("maxSpeed") Integer maxSpeed,
            @Param("minFuelConsumptionMixed") Double minFuelConsumptionMixed,
            @Param("maxFuelConsumptionMixed") Double maxFuelConsumptionMixed,
            Pageable pageable
    );
}
