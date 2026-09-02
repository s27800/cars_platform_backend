package com.carsplatform.backend.common.security.crypto;


final class CryptoTestKeys {

    static final String DATA_KEY = "Y2Fycy1wbGF0Zm9ybS10ZXN0LWRhdGEta2V5LTAwMDE=";
    static final String INDEX_KEY = "Y2Fycy1wbGF0Zm9ybS10ZXN0LWluZGV4LWtleS0wMDE=";


    private CryptoTestKeys() {
    }


    static EncryptionProperties properties() {
        return properties(DATA_KEY, INDEX_KEY);
    }

    static EncryptionProperties properties(String key, String indexKey) {
        EncryptionProperties properties = new EncryptionProperties();

        properties.setKey(key);
        properties.setIndexKey(indexKey);
        properties.decodeAndValidate();

        return properties;
    }
}
