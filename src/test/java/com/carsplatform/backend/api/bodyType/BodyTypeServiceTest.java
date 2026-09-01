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
import java.util.UUID;

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
        testBodyType1 = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .name("Sedan")
                .build();

        testBodyType2 = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .name("SUV")
                .build();
    }


    @Nested
    @DisplayName("getAllBodyTypes")
    class GetAllBodyTypesTests {

        @Test
        @DisplayName("should return list of all body types")
        void getAllBodyTypes_BodyTypesExist_ReturnsList() {
            CarBodyTypeResponse response1 = CarBodyTypeResponse.builder()
                    .id(UUID.randomUUID())
                    .name("Sedan")
                    .build();

            CarBodyTypeResponse response2 = CarBodyTypeResponse.builder()
                    .id(UUID.randomUUID())
                    .name("SUV")
                    .build();

            when(repository.findAll()).thenReturn(List.of(testBodyType1, testBodyType2));
            when(mapper.toDto(testBodyType1)).thenReturn(response1);
            when(mapper.toDto(testBodyType2)).thenReturn(response2);

            List<CarBodyTypeResponse> result = bodyTypeService.getAllBodyTypes();
            assertThat(result).hasSize(2);
            assertThat(result).extracting(CarBodyTypeResponse::getName)
                    .containsExactly("Sedan", "SUV");

            verify(repository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no body types exist")
        void getAllBodyTypes_NoBodyTypes_ReturnsEmptyList() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<CarBodyTypeResponse> result = bodyTypeService.getAllBodyTypes();
            assertThat(result).isEmpty();
            verify(repository).findAll();
        }
    }
}
