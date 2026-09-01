package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class GenerationService {

    private final GenerationRepository repository;
    private final GenerationDetailsMapper mapper;


    @Transactional(readOnly = true)
    public GenerationDetailsResponse getGenerationDetailsById(UUID generationId) {
        Generation generation = repository.findById(generationId)
                .orElseThrow(() -> new ResourceNotFoundException("Generation", "id", generationId));

        return mapper.toDto(generation);
    }
}
