package tech.orbfin.api.gateway.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class ConfigFirebase {

    @Value("classpath:serviceAccount.json")
    private Resource serviceAccountFile;

    @Value("${config.firebase.databaseURL}")
    private String databaseURL;

    @Bean
    public FirebaseApp initializeFirebase() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountFile.getInputStream()))
                    .setDatabaseUrl(databaseURL)
                    .build();

            return FirebaseApp.initializeApp(options, "ORB");
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            throw new IllegalStateException("Error initializing Firebase", e);
        }
    }
}
