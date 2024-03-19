package tech.orbfin.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tech.orbfin.api.config.EnvConfig;

@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(EnvConfig.class)
public class AccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}
}
