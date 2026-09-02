package com.carsplatform.backend.common.security.crypto;

import jakarta.persistence.Converter;

import org.springframework.stereotype.Component;


/** Encrypts the {@code users.last_name} column. */
@Component
@Converter
public class EncryptedLastNameConverter extends EncryptedStringConverter {

    public EncryptedLastNameConverter(AesGcmEncryptor encryptor) {
        super(encryptor, "users.last_name");
    }
}
