package tech.orbfin.api.gateway.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import tech.orbfin.api.gateway.authorization.AuthEntryPoint;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@EnableWebFluxSecurity
@AllArgsConstructor
public class ConfigSecurity {
    private final AuthEntryPoint authEntryPoint;
    private final ConfigCORS configCORS;
//    @Autowired
//    private ReactiveRedisOperationsSessionRepository sessionRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
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

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowCredentials(true);
//        configuration.addAllowedOrigin("*");
//        configuration.addAllowedMethod("*");
//        configuration.setExposedHeaders(Arrays.asList("Authorization"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
}
