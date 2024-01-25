package tech.orbfin.api.gateway.configurations;

//import com.google.firebase.auth.FirebaseAuth;
import com.google.api.client.util.Value;
import io.netty.handler.codec.http.cors.CorsConfig;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.JwkSetUriJwtDecoderBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import tech.orbfin.api.gateway.authorization.AuthEntryPoint;

import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import com.auth0.jwk.JwkProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

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
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
//                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
//                .jwt(jwt -> jwt.publicKey(rsaPublicKey)))
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("https://the7ofdiamonds.development");
        configuration.addAllowedMethod("*");
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
