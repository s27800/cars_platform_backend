package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final ModelRepository repository;
    private final ModelDetailsMapper mapper;

    @Transactional(readOnly = true)
    public ModelDetailsResponse getModelDetailsById(Integer modelId) {
        if (repository.findById(modelId).isPresent())
            throw new ResourceNotFoundException("Model", "id", modelId);

        return mapper.toDto(repository.findById(modelId).get());
    }
}
