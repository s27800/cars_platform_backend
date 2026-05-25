package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ModelService Tests")
class ModelServiceTest {

    @Mock
    private ModelRepository repository;

    @Mock
    private ModelDetailsMapper mapper;

    @InjectMocks
    private ModelService modelService;

    private Model testModel;
    private Brand testBrand;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .id(1)
                .name("BMW")
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .id(1)
                .name("3 Series")
                .build();
    }


    @Nested
    @DisplayName("getModelDetailsById")
    class GetModelDetailsByIdTests {

        @Test
        @DisplayName("should return model details when model exists")
        void getModelDetailsById_ModelExists_ReturnsDetails() {

            // Create expected response
            ModelDetailsResponse expectedResponse = ModelDetailsResponse.builder()
                    .id(1)
                    .name("3 Series")
                    .description("A test model for unit testing")
                    .build();

            // Mock repository and mapper behavior
            when(repository.findById(1)).thenReturn(Optional.of(testModel));
            when(mapper.toDto(testModel)).thenReturn(expectedResponse);

            // Get model details by ID
            ModelDetailsResponse result = modelService.getModelDetailsById(1);

            // Verify results
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("3 Series");

            verify(repository).findById(1);
            verify(mapper).toDto(testModel);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when model does not exist")
        void getModelDetailsById_ModelNotFound_ThrowsException() {

            // Mock repository behavior
            when(repository.findById(999)).thenReturn(Optional.empty());

            // Get model details by ID and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> modelService.getModelDetailsById(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository).findById(999);
            verify(mapper, never()).toDto(any());
        }
    }
}
