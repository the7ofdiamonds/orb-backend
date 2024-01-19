package tech.orbfin.api.gateway.config;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import tech.orbfin.api.gateway.auth.AuthManager;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tech.orbfin.api.gateway.auth.filters.FilterJWT;
import tech.orbfin.api.gateway.auth.providers.ProviderJWT;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
private final AuthenticationManager authenticationManager;
private final AuthenticationProvider authenticationProvider;
private final FilterJWT filterJWT;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(Customizer-> Customizer.disable())
                .authorizeExchange(authRequest ->
                    authRequest
                            .pathMatchers(HttpMethod.GET, "/").permitAll()
                            .pathMatchers(HttpMethod.POST, "/").permitAll()
                            .pathMatchers(HttpMethod.GET, "/logout").permitAll()
                            .pathMatchers(HttpMethod.POST, "/logout").permitAll()
                            .pathMatchers(HttpMethod.GET, "/signup").permitAll()
                            .pathMatchers(HttpMethod.POST, "/signup").permitAll()
                            .pathMatchers(HttpMethod.GET, "/forgot-password").permitAll()
                            .pathMatchers(HttpMethod.POST, "/forgot-password").permitAll()
                            .pathMatchers(HttpMethod.GET, "/change-password").permitAll()
                            .pathMatchers(HttpMethod.POST, "/change-password").permitAll()
                            .anyExchange().authenticated()
                )
                .httpBasic(Customizer-> Customizer.disable());
//            .authenticationManager(authenticationManager)
//            .authenticationProvider(authenticationProvider)
//            .addFilterBefore(filterJWT, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                .authorizeExchange(exchanges -> exchanges
//                        .anyExchange().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
//                .a;
//        return http.build();
//    }

}
