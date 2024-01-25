package tech.orbfin.api.gateway.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class ConfigFirebase {

    @Value("${services.config.google}")
    private String firebaseConfigFile;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {

        FileInputStream serviceAccount =
                new FileInputStream(firebaseConfigFile);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://theorb-f3a48.firebaseio.com")
                .build();

        return FirebaseApp.initializeApp(options, "orbfin");
    }
}
