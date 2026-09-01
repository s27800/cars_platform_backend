package com.carsplatform.backend.api.tags;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface TagRepository extends JpaRepository<Tag, UUID> {

}
