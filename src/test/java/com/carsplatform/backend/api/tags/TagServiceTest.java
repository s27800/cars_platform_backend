package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;
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
@DisplayName("TagService Tests")
class TagServiceTest {

    @Mock
    private TagRepository repository;

    @Mock
    private TagMapper mapper;

    @InjectMocks
    private TagService tagService;

    private Tag testTag1;
    private Tag testTag2;

    @BeforeEach
    void setUp() {

        // Create test tags
        testTag1 = TestDataFactory.defaultTag()
                .id(UUID.randomUUID())
                .name("Electric")
                .build();

        testTag2 = TestDataFactory.defaultTag()
                .id(UUID.randomUUID())
                .name("Hybrid")
                .build();
    }


    @Nested
    @DisplayName("getAllTags")
    class GetAllTagsTests {

        @Test
        @DisplayName("should return list of all tags")
        void getAllTags_TagsExist_ReturnsList() {

            // Create test tags
            CarTagResponse response1 = CarTagResponse.builder()
                    .id(UUID.randomUUID())
                    .name("Electric")
                    .build();

            CarTagResponse response2 = CarTagResponse.builder()
                    .id(UUID.randomUUID())
                    .name("Hybrid")
                    .build();

            // Prepare mocks
            when(repository.findAll()).thenReturn(List.of(testTag1, testTag2));
            when(mapper.toDto(testTag1)).thenReturn(response1);
            when(mapper.toDto(testTag2)).thenReturn(response2);

            // Get all tags
            List<CarTagResponse> result = tagService.getAllTags();

            // Verify results -> all tags are returned
            assertThat(result).hasSize(2);
            assertThat(result).extracting(CarTagResponse::getName)
                    .containsExactly("Electric", "Hybrid");

            verify(repository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no tags exist")
        void getAllTags_NoTags_ReturnsEmptyList() {

            // Prepare mocks
            when(repository.findAll()).thenReturn(Collections.emptyList());

            // Get all tags
            List<CarTagResponse> result = tagService.getAllTags();

            // Verify results -> empty list is returned
            assertThat(result).isEmpty();
            verify(repository).findAll();
        }
    }
}
