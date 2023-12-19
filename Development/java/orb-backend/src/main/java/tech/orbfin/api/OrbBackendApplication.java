package tech.orbfin.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "tech.orbfin.orbfin")
public class OrbBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrbBackendApplication.class, args);
	}
}
