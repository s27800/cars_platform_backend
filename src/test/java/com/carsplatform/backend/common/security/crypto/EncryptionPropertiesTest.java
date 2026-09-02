package com.carsplatform.backend.common.security.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;


@DisplayName("EncryptionProperties Tests")
class EncryptionPropertiesTest {

    @Test
    @DisplayName("accepts two different 256-bit keys")
    void validKeys_AreAccepted() {
        EncryptionProperties properties = CryptoTestKeys.properties();

        assertThat(properties.getDataKeyBytes()).hasSize(32);
        assertThat(properties.getIndexKeyBytes()).hasSize(32);
    }

    @Test
    @DisplayName("refuses to start without a key")
    void missingKey_Throws() {
        assertThatThrownBy(() -> CryptoTestKeys.properties(null, CryptoTestKeys.INDEX_KEY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("app.encryption.key");
    }

    @Test
    @DisplayName("refuses a key that is not Base64")
    void malformedKey_Throws() {
        assertThatThrownBy(() -> CryptoTestKeys.properties("not base64 at all!", CryptoTestKeys.INDEX_KEY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Base64");
    }

    @Test
    @DisplayName("refuses a key that is not 32 bytes long")
    void shortKey_Throws() {
        String tooShort = Base64.getEncoder().encodeToString(new byte[16]);

        assertThatThrownBy(() -> CryptoTestKeys.properties(tooShort, CryptoTestKeys.INDEX_KEY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32 bytes");
    }

    @Test
    @DisplayName("refuses to use one key for both encryption and the index")
    void sameKeyTwice_Throws() {
        assertThatThrownBy(() -> CryptoTestKeys.properties(CryptoTestKeys.DATA_KEY, CryptoTestKeys.DATA_KEY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("two different keys");
    }

    @Test
    @DisplayName("hands out a copy of the key bytes")
    void getKeyBytes_ReturnsDefensiveCopy() {
        EncryptionProperties properties = CryptoTestKeys.properties();
        byte[] first = properties.getDataKeyBytes();

        first[0] ^= 0x01;

        assertThat(properties.getDataKeyBytes()).isNotEqualTo(first);
    }
}
