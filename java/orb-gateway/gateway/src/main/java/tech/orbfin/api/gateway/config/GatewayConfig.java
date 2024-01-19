package tech.orbfin.api.gateway.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(@NotNull RouteLocatorBuilder builder) {
        return builder.routes()
                .route("home_route", r -> r
                        .path("/")
                        .uri("http://localhost:8080"))
                .route("signup_route", r -> r
                        .path("/signup")
                        .uri("http://localhost:8080"))
                .route("logout_route", r -> r
                        .path("/logout")
                        .uri("http://localhost:8080"))
                .route("forgot_password_route", r -> r
                        .path("/forgot-password")
                        .uri("http://localhost:8080"))
                .route("default_route", r -> r
                        .path("/**")
                        .uri("http://localhost:8080"))
                .build();
    }
}
