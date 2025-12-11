package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final IModelRepository repository;
    private final IModelDetailsMapper mapper;

    @Transactional(readOnly = true)
    public ModelDetailsResponse getModelDetailsById(Integer modelId) {
        if (repository.findById(modelId).isPresent())
            throw new ResourceNotFoundException("Brand", "id", modelId);

        return mapper.toDto(repository.findById(modelId).get());
    }
}
