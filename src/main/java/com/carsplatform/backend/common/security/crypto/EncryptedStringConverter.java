package com.carsplatform.backend.common.security.crypto;

import jakarta.persistence.AttributeConverter;


/**
 * Encrypts a text column on the way to the database and decrypts it on the way back. Services,
 * mappers and validation above it keep working on the plain value.
 *
 * Each subclass names the column it guards, and that name goes to AES-GCM as additional
 * authenticated data, so a ciphertext lifted from one column into another fails to decrypt.
 * Hibernate builds the subclasses through Spring's bean container, which is what lets them
 * take the encryptor as a constructor argument.
 */
public abstract class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final AesGcmEncryptor encryptor;
    private final String context;


    protected EncryptedStringConverter(AesGcmEncryptor encryptor, String context) {
        this.encryptor = encryptor;
        this.context = context;
    }


    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptor.encrypt(attribute, context);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptor.decrypt(dbData, context);
    }
}
