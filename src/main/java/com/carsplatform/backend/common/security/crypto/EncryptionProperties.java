package com.carsplatform.backend.common.security.crypto;

import jakarta.annotation.PostConstruct;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;


/**
 * The two Base64 encoded keys protecting personal data at rest, both read from the environment
 * and never from the repository: {@code app.encryption.key} encrypts the values,
 * {@code app.encryption.index-key} keys the blind index over the e-mail address.
 *
 * Keeping them apart matters. The index is deterministic, so whoever holds its key can confirm
 * a guess about an address, and that must not also give them the key that decrypts the rest.
 *
 * Both are validated on startup, so a missing or malformed key stops the application before it
 * writes anything nobody can read back.
 */
@Component
@ConfigurationProperties(prefix = "app.encryption")
@Getter
@Setter
public class EncryptionProperties {

    /** AES-256 needs exactly 32 bytes. */
    private static final int KEY_LENGTH_IN_BYTES = 32;

    private String key;

    private String indexKey;

    private byte[] dataKeyBytes;
    private byte[] indexKeyBytes;


    @PostConstruct
    void decodeAndValidate() {
        dataKeyBytes = decode(key, "app.encryption.key");
        indexKeyBytes = decode(indexKey, "app.encryption.index-key");

        if (Arrays.equals(dataKeyBytes, indexKeyBytes))
            throw new IllegalStateException(
                    "app.encryption.key and app.encryption.index-key must be two different keys.");
    }

    public byte[] getDataKeyBytes() {
        return dataKeyBytes.clone();
    }

    public byte[] getIndexKeyBytes() {
        return indexKeyBytes.clone();
    }

    private static byte[] decode(String value, String propertyName) {
        if (value == null || value.isBlank())
            throw new IllegalStateException(propertyName + " is not set - see .env.example.");

        byte[] decoded;

        try {
            decoded = Base64.getDecoder().decode(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(propertyName + " is not valid Base64.", ex);
        }

        if (decoded.length != KEY_LENGTH_IN_BYTES)
            throw new IllegalStateException(propertyName + " must decode to exactly "
                    + KEY_LENGTH_IN_BYTES + " bytes, got " + decoded.length + ".");

        return decoded;
    }
}
