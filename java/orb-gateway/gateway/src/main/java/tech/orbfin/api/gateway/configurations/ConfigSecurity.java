package tech.orbfin.api.gateway.configurations;

import tech.orbfin.api.gateway.authorization.AuthEntryPoint;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import reactor.core.publisher.Mono;

@Configuration
@EnableRedisWebSession
@EnableWebFluxSecurity
@AllArgsConstructor
public class ConfigSecurity {
private final AuthEntryPoint authEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(authEntryPoint)
                )
                .authorizeExchange(authorizeExchange -> authorizeExchange
                        .pathMatchers(HttpMethod.POST, "/signup", "/", "/logout", "/change-password", "/forgot-password")
                        .permitAll()
                        .anyExchange().authenticated()
                );

        return http.build();
    }
}
