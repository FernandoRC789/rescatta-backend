package com.rescatta.backend.common.storage;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StorageConfigTest {

    @Test
    void buildsCloudinaryClientWithDummyConfig() {
        StorageProperties properties = new StorageProperties(
                "cloudinary",
                null,
                null,
                null,
                null,
                new StorageProperties.Cloudinary("demo", "key", "secret", "rescatta")
        );

        assertNotNull(new CloudinaryStorageConfig().cloudinary(properties));
    }

    @Test
    void buildsR2ClientWithDummyConfig() {
        StorageProperties properties = new StorageProperties(
                "r2",
                null,
                null,
                "https://pub-demo.r2.dev",
                new StorageProperties.R2(
                        "https://demo.r2.cloudflarestorage.com",
                        "rescatta-images",
                        "access",
                        "secret",
                        "auto"
                ),
                null
        );

        try (S3Client client = new R2StorageConfig().r2S3Client(properties)) {
            assertNotNull(client);
        }
    }
}
