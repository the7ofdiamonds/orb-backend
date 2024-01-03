package tech.orbfin.api.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tech.orbfin.api.login.config.EnvConfig;
//import tech.orbfin.api.login.config.RedisConfig;

@SpringBootApplication
@EnableConfigurationProperties(EnvConfig.class)
public class LoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginApplication.class, args);
	}
}
