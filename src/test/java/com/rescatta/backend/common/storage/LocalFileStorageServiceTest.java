package com.rescatta.backend.common.storage;

import com.rescatta.backend.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalFileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storesWithSafeExtensionAndDeletesWithinBasePath() throws Exception {
        LocalFileStorageService storage = new LocalFileStorageService(properties());

        String url = storage.store(jpegFile(), "reports");

        assertTrue(url.startsWith("/uploads/reports/"));
        assertTrue(url.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve("reports").resolve(fileName(url))));

        storage.delete(url);

        assertFalse(Files.exists(tempDir.resolve("reports").resolve(fileName(url))));
    }

    @Test
    void rejectsPathTraversalOnDelete() {
        LocalFileStorageService storage = new LocalFileStorageService(properties());

        assertThrows(BadRequestException.class, () -> storage.delete("/uploads/../secret.txt"));
    }

    private StorageProperties properties() {
        return new StorageProperties(
                "local",
                tempDir.toString(),
                "/uploads",
                "",
                null,
                null
        );
    }

    private MockMultipartFile jpegFile() {
        byte[] bytes = new byte[32];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xD8;
        bytes[2] = (byte) 0xFF;
        bytes[3] = (byte) 0xE0;
        return new MockMultipartFile("photos", "foto.html", "image/jpeg", bytes);
    }

    private String fileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
