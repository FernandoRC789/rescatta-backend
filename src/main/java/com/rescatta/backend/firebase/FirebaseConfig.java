package com.rescatta.backend.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {

        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");

        if (firebaseJson == null || firebaseJson.isBlank()) {
            throw new IllegalStateException(
                    "Falta la variable de entorno FIREBASE_SERVICE_ACCOUNT_JSON"
            );
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(
                        firebaseJson.getBytes(StandardCharsets.UTF_8)
                )
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        FirebaseApp.initializeApp(options);

        System.out.println("Firebase Admin SDK inicializado correctamente");
    }
}