package com.carsplatform.backend.api.generations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGenerationRepository extends JpaRepository<Generation, Integer> {
}
