package tech.orbfin.api.gateway.configurations;

import lombok.AllArgsConstructor;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.WebFilter;
import tech.orbfin.api.gateway.authorization.filters.FilterAuth;
import tech.orbfin.api.gateway.authorization.filters.FilterJWT;

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class ConfigSecurity {
    private final FilterAuth filterJWT;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(Customizer -> Customizer.disable())
                .httpBasic(Customizer -> Customizer.disable());
//                .addFilterAt((WebFilter) filterJWT, SecurityWebFiltersOrder.AUTHENTICATION)
//                .authorizeExchange(authRequest ->
//                        authRequest
//                                .pathMatchers("/", "signup", "change-password", "forgot-password").permitAll()
//                                .anyExchange().authenticated()
//                );

        return http.build();
    }
}
