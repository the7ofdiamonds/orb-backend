package tech.orbfin.api.gateway.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import tech.orbfin.api.gateway.authentication.AuthEntryPoint;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class ConfigSecurity {
    private final AuthEntryPoint authEntryPoint;
    @Autowired
    private final ConfigCORS configCORS;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
//                .x509(Customizer -> Customizer.authenticationManager())
                .addFilterAt(configCORS.corsWebFilter(), SecurityWebFiltersOrder.CORS)
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
//                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
//                .jwt(jwt -> jwt.publicKey(rsaPublicKey)))
//                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
//                        .authenticationEntryPoint(authEntryPoint)
//                )
                .authorizeExchange(authorizeExchange -> authorizeExchange
                        .anyExchange().permitAll()
                );

        return http.build();
    }
}
