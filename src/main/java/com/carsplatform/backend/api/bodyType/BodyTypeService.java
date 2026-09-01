package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BodyTypeService {

    private final BodyTypeRepository repository;
    private final BodyTypeMapper mapper;


    @Transactional(readOnly = true)
    public List<CarBodyTypeResponse> getAllBodyTypes() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
