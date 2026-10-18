package com.example.expense.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class ReceiptStorageServiceTest {

    @TempDir
    Path tempDir;

    private ReceiptStorageService service() {
        return new ReceiptStorageService(tempDir.toString());
    }

    private static MockMultipartFile file(String name, int size) {
        return new MockMultipartFile("receipt", name, "application/octet-stream", new byte[size]);
    }

    @Test
    void acceptsPdfJpgAndPng() {
        assertNull(service().validate(file("receipt.pdf", 10)));
        assertNull(service().validate(file("photo.JPG", 10)));
        assertNull(service().validate(file("scan.png", 10)));
    }

    @Test
    void rejectsMissingOrEmptyFile() {
        assertEquals("Receipt is required", service().validate(null));
        assertEquals("Receipt is required", service().validate(file("receipt.pdf", 0)));
    }

    @Test
    void rejectsOtherFileTypes() {
        assertEquals("Receipt must be a PDF, JPG or PNG file", service().validate(file("notes.txt", 10)));
    }

    @Test
    void rejectsFilesOverTwoMegabytes() {
        assertEquals("Receipt must be 2 MB or smaller",
                service().validate(file("big.pdf", 2 * 1024 * 1024 + 1)));
    }

    @Test
    void storesFileUnderGeneratedName() throws IOException {
        ReceiptStorageService service = service();
        String name = service.store(file("my receipt.pdf", 10));
        assertTrue(name.endsWith(".pdf"));
        assertTrue(Files.exists(tempDir.resolve(name)));
        assertTrue(service.load(name).exists());
    }
}