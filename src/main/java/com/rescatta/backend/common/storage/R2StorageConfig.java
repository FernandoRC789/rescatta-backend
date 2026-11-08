package com.rescatta.backend.common.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Cliente S3 apuntando a Cloudflare R2.
 *
 * <p>R2 es compatible con la API de S3, así que reutilizamos el SDK de AWS. El endpoint
 * tiene la forma {@code https://&lt;account-id&gt;.r2.cloudflarestorage.com} y la región
 * es {@code auto}.
 */
@Configuration
@ConditionalOnProperty(name = "rescatta.storage.provider", havingValue = "r2")
public class R2StorageConfig {

    @Bean
    public S3Client r2S3Client(StorageProperties properties) {
        StorageProperties.R2 r2 = properties.r2();
        if (r2 == null) {
            throw new IllegalStateException("Falta la configuración rescatta.storage.r2.");
        }

        require(r2.endpoint(), "R2_ENDPOINT");
        require(r2.bucket(), "R2_BUCKET");
        require(r2.accessKey(), "R2_ACCESS_KEY");
        require(r2.secretKey(), "R2_SECRET_KEY");

        String region = (r2.region() == null || r2.region().isBlank()) ? "auto" : r2.region();

        return S3Client.builder()
                .endpointOverride(URI.create(r2.endpoint()))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(r2.accessKey(), r2.secretKey())))
                .forcePathStyle(true)
                // Evita cargar el cliente Apache 5 del SDK, que exige una versión de
                // httpclient5 más nueva que la que traen Firebase/Cloudinary.
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                // R2 no implementa todos los checksums opcionales de AWS S3.
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .build();
    }

    private void require(String value, String envName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno " + envName + " para Cloudflare R2.");
        }
    }
}
