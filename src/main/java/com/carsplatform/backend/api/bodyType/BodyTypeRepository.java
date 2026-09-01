package com.carsplatform.backend.api.bodyType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface BodyTypeRepository extends JpaRepository<BodyType, UUID> {

}
