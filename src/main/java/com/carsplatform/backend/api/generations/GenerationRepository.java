package com.carsplatform.backend.api.generations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface GenerationRepository extends JpaRepository<Generation, UUID> {

}
