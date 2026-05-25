package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;
import com.carsplatform.backend.api.models.Model;
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
@DisplayName("GenerationService Tests")
class GenerationServiceTest {

    @Mock
    private GenerationRepository repository;

    @Mock
    private GenerationDetailsMapper mapper;

    @InjectMocks
    private GenerationService generationService;

    private Generation testGeneration;
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

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .id(1)
                .name("G20")
                .build();
    }


    @Nested
    @DisplayName("getGenerationDetailsById")
    class GetGenerationDetailsByIdTests {

        @Test
        @DisplayName("should return generation details when generation exists")
        void getGenerationDetailsById_GenerationExists_ReturnsDetails() {

            // Create expected response
            GenerationDetailsResponse expectedResponse = GenerationDetailsResponse.builder()
                    .id(1)
                    .name("G20")
                    .build();

            // Mock repository and mapper behavior
            when(repository.findById(1)).thenReturn(Optional.of(testGeneration));
            when(mapper.toDto(testGeneration)).thenReturn(expectedResponse);

            // Get generation details by ID
            GenerationDetailsResponse result = generationService.getGenerationDetailsById(1);

            // Verify results -> correct response is returned
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("G20");

            verify(repository).findById(1);
            verify(mapper).toDto(testGeneration);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when generation does not exist")
        void getGenerationDetailsById_GenerationNotFound_ThrowsException() {

            // Mock repository
            when(repository.findById(999)).thenReturn(Optional.empty());

            // Get generation details by ID and verify results -> ResourceNotFoundException thrown
            assertThatThrownBy(() -> generationService.getGenerationDetailsById(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository).findById(999);
            verify(mapper, never()).toDto(any());
        }
    }
}
