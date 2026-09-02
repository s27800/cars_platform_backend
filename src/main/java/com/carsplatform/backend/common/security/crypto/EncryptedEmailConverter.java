package com.carsplatform.backend.common.security.crypto;

import jakarta.persistence.Converter;

import org.springframework.stereotype.Component;


/** Encrypts the {@code users.email} column. */
@Component
@Converter
public class EncryptedEmailConverter extends EncryptedStringConverter {

    public EncryptedEmailConverter(AesGcmEncryptor encryptor) {
        super(encryptor, "users.email");
    }
}
