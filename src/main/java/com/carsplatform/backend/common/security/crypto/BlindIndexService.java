package com.carsplatform.backend.common.security.crypto;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.Locale;


/**
 * Keyed hash of the e-mail address, kept in {@code users.email_hash}. The ciphertext of an
 * address differs on every write, so neither the unique constraint nor a lookup can use the
 * encrypted column; this hash is deterministic and keyed separately from the data.
 *
 * Values are trimmed and lower-cased, so {@code Jan@Example.com} and {@code jan@example.com}
 * count as the same address.
 */
@Service
public class BlindIndexService {

    private static final String ALGORITHM = "HmacSHA256";

    private final SecretKeySpec key;


    public BlindIndexService(EncryptionProperties properties) {
        this.key = new SecretKeySpec(properties.getIndexKeyBytes(), ALGORITHM);
    }


    /**
     * @return 64 lower-case hexadecimal characters, {@code null} for a {@code null} address
     */
    public String hash(String value) {
        if (value == null)
            return null;

        String normalized = value.trim().toLowerCase(Locale.ROOT);

        try {
            Mac mac = Mac.getInstance(ALGORITHM);

            mac.init(key);

            return HexFormat.of().formatHex(mac.doFinal(normalized.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException ex) {
            throw new EncryptionException("Failed to compute a blind index.", ex);
        }
    }
}
