package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository repository;
    private final TagMapper mapper;

    @Transactional(readOnly = true)
    public List<CarTagResponse> getAllTags() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
