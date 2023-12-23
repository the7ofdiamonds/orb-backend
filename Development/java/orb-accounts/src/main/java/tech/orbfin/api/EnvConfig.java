package tech.orbfin.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class EnvConfig {

    private String url;
    private String username;
    private String password;
    private String stripeAPIKey;

    public String getDatasourceUrl() {
        return this.url;
    }

    public void setDatasourceUrl(String url) {
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

    public EnvConfig() {
        this.url = System.getenv("DATABASE_URL");
        this.username = System.getenv("DATABASE_USERNAME");
        this.password = System.getenv("DATABASE_PASSWORD");
        this.stripeAPIKey = System.getenv("STRIPE_API_KEY");
    }
}
