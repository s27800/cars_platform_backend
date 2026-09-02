package com.carsplatform.backend.common.security.crypto;

import jakarta.persistence.Converter;

import org.springframework.stereotype.Component;


/** Encrypts the {@code users.first_name} column. */
@Component
@Converter
public class EncryptedFirstNameConverter extends EncryptedStringConverter {

    public EncryptedFirstNameConverter(AesGcmEncryptor encryptor) {
        super(encryptor, "users.first_name");
    }
}
