package com.carsplatform.backend.api.cars;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    @Query("SELECT DISTINCT c FROM Car c " +
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
            "LEFT JOIN FETCH c.images " +
            "LEFT JOIN FETCH c.tags " +
            "WHERE c.id = :id")
    Optional<Car> findByIdWithDetails(@Param("id") UUID id);

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
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.generation.model.brand.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.generation.model.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.generation.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.engine.engineCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
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
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.generation.model.brand.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.generation.model.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.generation.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.engine.engineCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
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
            @Param("search") String search,
            @Param("brandIds") List<UUID> brandIds,
            @Param("modelIds") List<UUID> modelIds,
            @Param("generationIds") List<UUID> generationIds,
            @Param("bodyTypeIds") List<UUID> bodyTypeIds,
            @Param("tagIds") List<UUID> tagIds,
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

    @Query("SELECT c FROM Car c " +
            "LEFT JOIN FETCH c.tags " +
            "LEFT JOIN FETCH c.generation g " +
            "LEFT JOIN FETCH g.model m " +
            "LEFT JOIN FETCH m.brand " +
            "LEFT JOIN FETCH c.bodyType " +
            "WHERE c.id = :id")
    Optional<Car> findByIdWithTagsAndRelations(@Param("id") UUID id);

    @Query("SELECT DISTINCT c.id FROM Car c " +
            "LEFT JOIN c.tags t " +
            "LEFT JOIN c.generation g " +
            "LEFT JOIN g.model m " +
            "LEFT JOIN m.brand b " +
            "WHERE c.id != :carId " +
            "AND ((:tagIds IS NOT NULL AND t.id IN :tagIds) " +
            "OR b.id = :brandId " +
            "OR c.bodyType.id = :bodyTypeId)")
    List<UUID> findSimilarCarIds(
            @Param("carId") UUID carId,
            @Param("tagIds") Set<UUID> tagIds,
            @Param("brandId") UUID brandId,
            @Param("bodyTypeId") UUID bodyTypeId,
            Pageable pageable
    );

    @Query("SELECT c FROM Car c " +
            "LEFT JOIN FETCH c.engine " +
            "LEFT JOIN FETCH c.bodyType " +
            "LEFT JOIN FETCH c.transmission " +
            "LEFT JOIN FETCH c.images " +
            "LEFT JOIN FETCH c.generation g " +
            "LEFT JOIN FETCH g.model m " +
            "LEFT JOIN FETCH m.brand " +
            "WHERE c.id IN :ids")
    List<Car> findByIdsWithRelations(@Param("ids") List<UUID> ids);
}
