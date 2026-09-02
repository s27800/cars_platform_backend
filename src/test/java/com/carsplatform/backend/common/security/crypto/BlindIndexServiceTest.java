package com.carsplatform.backend.common.security.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


@DisplayName("BlindIndexService Tests")
class BlindIndexServiceTest {

    private final BlindIndexService blindIndexService = new BlindIndexService(CryptoTestKeys.properties());


    @Test
    @DisplayName("returns the same index for the same address")
    void hash_SameValue_ReturnsSameIndex() {
        assertThat(blindIndexService.hash("jan@example.com"))
                .isEqualTo(blindIndexService.hash("jan@example.com"));
    }

    @Test
    @DisplayName("ignores case and surrounding spaces")
    void hash_DifferentCaseAndSpacing_ReturnsSameIndex() {
        assertThat(blindIndexService.hash("  Jan@Example.COM  "))
                .isEqualTo(blindIndexService.hash("jan@example.com"));
    }

    @Test
    @DisplayName("returns different indexes for different addresses")
    void hash_DifferentValues_ReturnDifferentIndexes() {
        assertThat(blindIndexService.hash("jan@example.com"))
                .isNotEqualTo(blindIndexService.hash("anna@example.com"));
    }

    @Test
    @DisplayName("returns 64 hexadecimal characters without the address in them")
    void hash_ReturnsHexDigestWithoutPlaintext() {
        assertThat(blindIndexService.hash("jan@example.com"))
                .hasSize(64)
                .matches("[0-9a-f]{64}")
                .doesNotContain("jan");
    }

    @Test
    @DisplayName("returns a different index under a different key")
    void hash_DifferentKey_ReturnsDifferentIndex() {
        BlindIndexService other = new BlindIndexService(
                CryptoTestKeys.properties(CryptoTestKeys.INDEX_KEY, CryptoTestKeys.DATA_KEY));

        assertThat(blindIndexService.hash("jan@example.com"))
                .isNotEqualTo(other.hash("jan@example.com"));
    }

    @Test
    @DisplayName("returns null for a missing address")
    void hash_NullValue_ReturnsNull() {
        assertThat(blindIndexService.hash(null)).isNull();
    }
}
