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
import java.util.UUID;

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
        testBrand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();
        testModel = TestDataFactory.defaultModel(testBrand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .build();
    }


    @Nested
    @DisplayName("getModelDetailsById")
    class GetModelDetailsByIdTests {

        @Test
        @DisplayName("should return model details when model exists")
        void getModelDetailsById_ModelExists_ReturnsDetails() {
            ModelDetailsResponse expectedResponse = ModelDetailsResponse.builder()
                    .id(UUID.randomUUID())
                    .name("3 Series")
                    .description("A test model for unit testing")
                    .build();

            when(repository.findById(testModel.getId())).thenReturn(Optional.of(testModel));
            when(mapper.toDto(testModel)).thenReturn(expectedResponse);

            ModelDetailsResponse result = modelService.getModelDetailsById(testModel.getId());
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(expectedResponse.getId());
            assertThat(result.getName()).isEqualTo("3 Series");

            verify(repository).findById(testModel.getId());
            verify(mapper).toDto(testModel);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when model does not exist")
        void getModelDetailsById_ModelNotFound_ThrowsException() {
            UUID nonExistentId = UUID.randomUUID();

            when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> modelService.getModelDetailsById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository).findById(nonExistentId);
            verify(mapper, never()).toDto(any());
        }
    }
}
