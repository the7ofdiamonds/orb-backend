package tech.orbfin.api.gateway.configurations;

import lombok.extern.slf4j.Slf4j;
import tech.orbfin.api.gateway.services.ServiceAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tech.orbfin.api.gateway.authentication.AuthEntryPoint;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.orbfin.api.gateway.services.ServiceAuthLogout;
import tech.orbfin.api.gateway.services.ServiceToken;

@Slf4j
@AllArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class ConfigSecurity {
    private final AuthEntryPoint authEntryPoint;
    @Autowired
    private final ConfigCORS configCORS;

    private final ServiceAuthLogout serviceAuthLogot;

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

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
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessHandler(logoutSuccessHandler())
                );

        return http.build();
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        return (exchange, authentication) -> {
            SecurityContextHolder.clearContext();
            String token = ServiceToken.getToken(exchange.getExchange());

            serviceAuthLogot.logout(token);
            return Mono.empty();
        };
    }
}
