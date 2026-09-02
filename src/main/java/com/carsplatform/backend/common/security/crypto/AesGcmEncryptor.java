package com.carsplatform.backend.common.security.crypto;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;


/**
 * AES-256 in GCM mode. The mode is authenticated, so a value modified in the database fails to
 * decrypt rather than coming back changed.
 *
 * A new initialisation vector is drawn per value, which makes two encryptions of the same text
 * differ. It is not secret and is stored with the ciphertext as {@code v1:Base64(iv ||
 * ciphertext || tag)}; the prefix names the key generation, leaving room for a later rotation.
 * The column name is passed as additional authenticated data, which ties each value to the
 * column it was written to.
 */
@Component
public class AesGcmEncryptor {

    static final String PREFIX = "v1:";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final int IV_LENGTH_IN_BYTES = 12;
    private static final int TAG_LENGTH_IN_BITS = 128;

    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();


    public AesGcmEncryptor(EncryptionProperties properties) {
        this.key = new SecretKeySpec(properties.getDataKeyBytes(), ALGORITHM);
    }


    /**
     * @param context the column name, which has to be repeated on {@link #decrypt}
     */
    public String encrypt(String plaintext, String context) {
        if (plaintext == null)
            return null;

        byte[] iv = new byte[IV_LENGTH_IN_BYTES];

        random.nextBytes(iv);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_IN_BITS, iv));
            cipher.updateAAD(context.getBytes(StandardCharsets.UTF_8));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] envelope = new byte[iv.length + ciphertext.length];

            System.arraycopy(iv, 0, envelope, 0, iv.length);
            System.arraycopy(ciphertext, 0, envelope, iv.length, ciphertext.length);

            return PREFIX + Base64.getEncoder().encodeToString(envelope);
        } catch (GeneralSecurityException ex) {
            throw new EncryptionException("Failed to encrypt a value for " + context + ".", ex);
        }
    }

    public String decrypt(String envelope, String context) {
        if (envelope == null)
            return null;

        if (!envelope.startsWith(PREFIX))
            throw new EncryptionException("The value stored in " + context + " is not encrypted."
                    + " The database was filled with plain text, which this application never"
                    + " writes - re-create it from docker/init.");

        byte[] decoded;

        try {
            decoded = Base64.getDecoder().decode(envelope.substring(PREFIX.length()));
        } catch (IllegalArgumentException ex) {
            throw new EncryptionException("The value stored in " + context + " is not valid Base64.", ex);
        }

        if (decoded.length <= IV_LENGTH_IN_BYTES)
            throw new EncryptionException("The value stored in " + context + " is too short to be"
                    + " an AES-GCM envelope.");

        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH_IN_BYTES);
        byte[] ciphertext = Arrays.copyOfRange(decoded, IV_LENGTH_IN_BYTES, decoded.length);

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_IN_BITS, iv));
            cipher.updateAAD(context.getBytes(StandardCharsets.UTF_8));

            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new EncryptionException("Failed to decrypt a value from " + context
                    + " - it was modified, or it was written with a different key.", ex);
        }
    }
}
