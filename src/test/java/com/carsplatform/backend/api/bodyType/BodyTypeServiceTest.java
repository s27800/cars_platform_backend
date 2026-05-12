package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("BodyTypeService Tests")
class BodyTypeServiceTest {

    @Mock
    private BodyTypeRepository repository;

    @Mock
    private BodyTypeMapper mapper;

    @InjectMocks
    private BodyTypeService bodyTypeService;

    private BodyType testBodyType1;
    private BodyType testBodyType2;

    @BeforeEach
    void setUp() {

        // Create test body types
        testBodyType1 = TestDataFactory.defaultBodyType()
                .id(1)
                .name("Sedan")
                .build();

        testBodyType2 = TestDataFactory.defaultBodyType()
                .id(2)
                .name("SUV")
                .build();
    }


    @Nested
    @DisplayName("getAllBodyTypes")
    class GetAllBodyTypesTests {

        @Test
        @DisplayName("should return list of all body types")
        void getAllBodyTypes_BodyTypesExist_ReturnsList() {

            // Create expected response DTOs
            CarBodyTypeResponse response1 = CarBodyTypeResponse.builder()
                    .id(1)
                    .name("Sedan")
                    .build();
                    
            CarBodyTypeResponse response2 = CarBodyTypeResponse.builder()
                    .id(2)
                    .name("SUV")
                    .build();

            // Mock repository and mapper behavior
            when(repository.findAll()).thenReturn(List.of(testBodyType1, testBodyType2));
            when(mapper.toDto(testBodyType1)).thenReturn(response1);
            when(mapper.toDto(testBodyType2)).thenReturn(response2);

            // Get all body types
            List<CarBodyTypeResponse> result = bodyTypeService.getAllBodyTypes();

            // Verify result -> correct list of body types returned
            assertThat(result).hasSize(2);
            assertThat(result).extracting(CarBodyTypeResponse::getName)
                    .containsExactly("Sedan", "SUV");

            verify(repository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no body types exist")
        void getAllBodyTypes_NoBodyTypes_ReturnsEmptyList() {

            // Mock repository behavior
            when(repository.findAll()).thenReturn(Collections.emptyList());

            // Get all body types
            List<CarBodyTypeResponse> result = bodyTypeService.getAllBodyTypes();

            // Verify result -> empty list returned
            assertThat(result).isEmpty();
            verify(repository).findAll();
        }
    }
}
