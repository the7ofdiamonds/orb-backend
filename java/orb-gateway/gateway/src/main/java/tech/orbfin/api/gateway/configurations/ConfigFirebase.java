package tech.orbfin.api.gateway.configurations;

import tech.orbfin.api.gateway.utils.JSON;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Setter
@Getter
@Slf4j
@Configuration
public class ConfigFirebase {

    @Value(value = "${config.firebase.serviceAccountFilePath}")
    private String serviceAccountFilePath;
    @Value(value = "${config.firebase.databaseURL}")
    private String databaseURL;

    private String readServiceAccountContent() {
        return JSON.readFileContent(serviceAccountFilePath);
    }

    @Bean
    protected String setServiceAccountID() throws Exception {
        return JSON.searchJsonValue(readServiceAccountContent(), "client_email");
    }

    @Bean
    public FirebaseApp initializeFirebase() throws Exception {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(serviceAccountFilePath)))
                    .setServiceAccountId(setServiceAccountID())
                    .setDatabaseUrl(databaseURL)
                    .build();

            return FirebaseApp.initializeApp(options, "ORB");
    }
}
