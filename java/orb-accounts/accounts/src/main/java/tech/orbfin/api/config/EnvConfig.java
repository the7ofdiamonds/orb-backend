package tech.orbfin.api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class EnvConfig {

    private String url = "jdbc:postgresql://localhost:5432/postgres";
    private String username = "postgres";
    private String password = "postgres";
    private String stripeAPIKey;
    private String googleClientID;
    private String googleClientSecret;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStripeAPIKey() {
        return this.stripeAPIKey;
    }

    public void setStripeAPIKey(String stripeAPIKey) {
        this.stripeAPIKey = stripeAPIKey;
    }

    public String getGoogleClientID(){return this.googleClientID;}

    public void setGoogleClientID(String googleClientID) {this.googleClientID = googleClientID;}

    public String getGoogleClientSecret() {return this.googleClientSecret;}

    public void setGoogleClientSecret(String googleClientSecret){this.googleClientSecret = googleClientSecret;}

    @PostConstruct
    public void init() {
        this.url = System.getenv().getOrDefault("DATABASE_URL", this.url);
        this.username = System.getenv().getOrDefault("DATABASE_USERNAME", this.username);
        this.password = System.getenv().getOrDefault("DATABASE_PASSWORD", this.password);
        this.stripeAPIKey = System.getenv().getOrDefault("STRIPE_API_KEY", this.stripeAPIKey);
        this.googleClientID = System.getenv().getOrDefault("GOOGLE_CLIENT_ID", this.googleClientID);
        this.googleClientSecret = System.getenv().getOrDefault("GOOGLE_CLIENT_SECRET", this.googleClientSecret);
    }
}
