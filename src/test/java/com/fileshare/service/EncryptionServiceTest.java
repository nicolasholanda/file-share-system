package com.fileshare.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        byte[] keyBytes = "12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        encryptionService = new EncryptionService(key);
    }

    @Test
    void encryptAndDecrypt_shouldReturnOriginalContent() {
        byte[] original = "Hello, FileShare!".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = encryptionService.encrypt(original);
        byte[] decrypted = encryptionService.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void encrypt_shouldProduceDifferentBytesFromOriginal() {
        byte[] original = "sensitive content".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = encryptionService.encrypt(original);

        assertThat(encrypted).isNotEqualTo(original);
    }

    @Test
    void encrypt_samePlaintext_shouldProduceDifferentCiphertexts() {
        byte[] original = "same content".getBytes(StandardCharsets.UTF_8);

        byte[] first = encryptionService.encrypt(original);
        byte[] second = encryptionService.encrypt(original);

        assertThat(first).isNotEqualTo(second);
    }
}
