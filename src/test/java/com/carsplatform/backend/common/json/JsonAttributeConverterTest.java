package com.carsplatform.backend.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("JsonAttributeConverter Tests")
class JsonAttributeConverterTest {

    @Mock
    private ObjectMapper objectMapper;

    private JsonAttributeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new JsonAttributeConverter(objectMapper);
    }


    @Nested
    @DisplayName("convertToDatabaseColumn")
    class ConvertToDatabaseColumnTests {

        @Test
        @DisplayName("should return null when input is null")
        void convertToDatabaseColumn_NullInput_ReturnsNull() {

            // Convert null to database column
            String result = converter.convertToDatabaseColumn(null);

            // Verify result is null
            assertThat(result).isNull();
            verifyNoInteractions(objectMapper);
        }

        @Test
        @DisplayName("should convert map to JSON string")
        void convertToDatabaseColumn_ValidMap_ReturnsJsonString() throws Exception {

            // Create test data
            Map<String, Object> inputMap = Map.of("key1", "value1", "key2", 123);

            // Mock ObjectMapper behavior
            when(objectMapper.writeValueAsString(inputMap)).thenReturn("{\"key1\":\"value1\",\"key2\":123}");

            // Convert map to database column
            String result = converter.convertToDatabaseColumn(inputMap);

            // Verify result is JSON string
            assertThat(result).isEqualTo("{\"key1\":\"value1\",\"key2\":123}");
            verify(objectMapper).writeValueAsString(inputMap);
        }

        @Test
        @DisplayName("should convert empty map to empty JSON object")
        void convertToDatabaseColumn_EmptyMap_ReturnsEmptyJsonObject() throws Exception {

            // Create empty map
            Map<String, Object> emptyMap = new HashMap<>();

            // Mock ObjectMapper behavior
            when(objectMapper.writeValueAsString(emptyMap)).thenReturn("{}");

            // Convert empty map to database column
            String result = converter.convertToDatabaseColumn(emptyMap);

            // Verify result is empty JSON object
            assertThat(result).isEqualTo("{}");
        }

        @Test
        @DisplayName("should throw RuntimeException when serialization fails")
        void convertToDatabaseColumn_SerializationError_ThrowsRuntimeException() throws Exception {

            // Create test data
            Map<String, Object> inputMap = Map.of("key", "value");

            // Mock ObjectMapper to throw exception
            when(objectMapper.writeValueAsString(any()))
                    .thenThrow(new JsonProcessingException("Serialization error") {});

            // Convert map and verify exception is thrown
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(inputMap))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error converting Map to JSON string");
        }
    }


    @Nested
    @DisplayName("convertToEntityAttribute")
    class ConvertToEntityAttributeTests {

        @Test
        @DisplayName("should return empty HashMap when input is null")
        void convertToEntityAttribute_NullInput_ReturnsEmptyHashMap() {

            // Convert null to entity attribute
            Map<String, Object> result = converter.convertToEntityAttribute(null);

            // Verify result is empty HashMap
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            assertThat(result).isInstanceOf(HashMap.class);
            verifyNoInteractions(objectMapper);
        }

        @Test
        @DisplayName("should convert JSON string to map")
        void convertToEntityAttribute_ValidJson_ReturnsMap() throws Exception {

            // Create test data
            String jsonString = "{\"key1\":\"value1\",\"key2\":123}";
            Map<String, Object> expectedMap = Map.of("key1", "value1", "key2", 123);

            // Mock ObjectMapper behavior
            when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                    .thenReturn(expectedMap);

            // Convert JSON string to entity attribute
            Map<String, Object> result = converter.convertToEntityAttribute(jsonString);

            // Verify result is correct map
            assertThat(result).isEqualTo(expectedMap);
        }

        @Test
        @DisplayName("should convert empty JSON object to empty map")
        void convertToEntityAttribute_EmptyJsonObject_ReturnsEmptyMap() throws Exception {

            // Create test data
            String emptyJson = "{}";
            Map<String, Object> emptyMap = new HashMap<>();

            // Mock ObjectMapper behavior
            when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                    .thenReturn(emptyMap);

            // Convert empty JSON to entity attribute
            Map<String, Object> result = converter.convertToEntityAttribute(emptyJson);

            // Verify result is empty map
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw RuntimeException when deserialization fails")
        void convertToEntityAttribute_DeserializationError_ThrowsRuntimeException() throws Exception {

            // Create invalid JSON
            String invalidJson = "not-valid-json";

            // Mock ObjectMapper to throw exception using doAnswer for checked exception
            when(objectMapper.readValue(anyString(), any(com.fasterxml.jackson.core.type.TypeReference.class)))
                    .thenAnswer(invocation -> {
                        throw new IOException("Deserialization error");
                    });

            // Convert JSON and verify result -> RuntimeException is thrown
            assertThatThrownBy(() -> converter.convertToEntityAttribute(invalidJson))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error converting JSON string to Map");
        }
    }
}
