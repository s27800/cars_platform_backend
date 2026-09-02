package com.carsplatform.backend.common.security.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;


@DisplayName("AesGcmEncryptor Tests")
class AesGcmEncryptorTest {

    private static final String CONTEXT = "users.email";

    private final AesGcmEncryptor encryptor = new AesGcmEncryptor(CryptoTestKeys.properties());


    @Nested
    @DisplayName("encrypt and decrypt")
    class RoundTripTests {

        @Test
        @DisplayName("decrypt returns what encrypt was given")
        void encryptThenDecrypt_ReturnsOriginalValue() {
            String result = encryptor.decrypt(encryptor.encrypt("jan@example.com", CONTEXT), CONTEXT);
            assertThat(result).isEqualTo("jan@example.com");
        }

        @Test
        @DisplayName("round-trips characters outside ASCII")
        void encryptThenDecrypt_NonAsciiValue_ReturnsOriginalValue() {
            String result = encryptor.decrypt(encryptor.encrypt("Zażółć gęślą jaźń", CONTEXT), CONTEXT);
            assertThat(result).isEqualTo("Zażółć gęślą jaźń");
        }

        @Test
        @DisplayName("round-trips an empty value")
        void encryptThenDecrypt_EmptyValue_ReturnsEmptyValue() {
            assertThat(encryptor.decrypt(encryptor.encrypt("", CONTEXT), CONTEXT)).isEmpty();
        }

        @Test
        @DisplayName("returns null for null in both directions")
        void nullValue_StaysNull() {
            assertThat(encryptor.encrypt(null, CONTEXT)).isNull();
            assertThat(encryptor.decrypt(null, CONTEXT)).isNull();
        }
    }


    @Nested
    @DisplayName("stored form")
    class EnvelopeTests {

        @Test
        @DisplayName("does not leave the plain value in the result")
        void encrypt_DoesNotLeakPlaintext() {
            String envelope = encryptor.encrypt("jan@example.com", CONTEXT);

            assertThat(envelope).startsWith("v1:").doesNotContain("jan@example.com");
        }

        @Test
        @DisplayName("produces a different ciphertext on every write")
        void encrypt_SameValueTwice_ProducesDifferentCiphertexts() {
            String first = encryptor.encrypt("jan@example.com", CONTEXT);
            String second = encryptor.encrypt("jan@example.com", CONTEXT);

            assertThat(first).isNotEqualTo(second);
            assertThat(encryptor.decrypt(first, CONTEXT)).isEqualTo(encryptor.decrypt(second, CONTEXT));
        }
    }


    @Nested
    @DisplayName("rejected input")
    class FailureTests {

        @Test
        @DisplayName("rejects a modified ciphertext")
        void decrypt_TamperedCiphertext_Throws() {
            String envelope = encryptor.encrypt("jan@example.com", CONTEXT);
            byte[] raw = Base64.getDecoder().decode(envelope.substring("v1:".length()));

            raw[raw.length - 1] ^= 0x01;

            String tampered = "v1:" + Base64.getEncoder().encodeToString(raw);

            assertThatThrownBy(() -> encryptor.decrypt(tampered, CONTEXT))
                    .isInstanceOf(EncryptionException.class);
        }

        @Test
        @DisplayName("rejects a ciphertext taken from another column")
        void decrypt_WrongContext_Throws() {
            String envelope = encryptor.encrypt("Jan", "users.first_name");

            assertThatThrownBy(() -> encryptor.decrypt(envelope, "users.last_name"))
                    .isInstanceOf(EncryptionException.class);
        }

        @Test
        @DisplayName("rejects a value written with another key")
        void decrypt_DifferentKey_Throws() {
            AesGcmEncryptor other = new AesGcmEncryptor(
                    CryptoTestKeys.properties(CryptoTestKeys.INDEX_KEY, CryptoTestKeys.DATA_KEY));
            String envelope = other.encrypt("jan@example.com", CONTEXT);

            assertThatThrownBy(() -> encryptor.decrypt(envelope, CONTEXT))
                    .isInstanceOf(EncryptionException.class);
        }

        @Test
        @DisplayName("reports plain text with a clear message")
        void decrypt_UnencryptedValue_ThrowsWithHint() {
            assertThatThrownBy(() -> encryptor.decrypt("jan@example.com", CONTEXT))
                    .isInstanceOf(EncryptionException.class)
                    .hasMessageContaining("not encrypted");
        }
    }
}
