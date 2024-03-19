//package tech.orbfin.api.gateway.configurations;
//
////import tech.orbfin.api.gateway.authentication.AuthEntryPoint;
//
//import org.springframework.http.HttpMethod;
//import tech.orbfin.api.gateway.authentication.AuthEntryPoint;
//import tech.orbfin.api.gateway.authentication.AuthManager;
//import tech.orbfin.api.gateway.model.request.RequestLogout;
//import tech.orbfin.api.gateway.services.ServiceAuthLogout;
//import tech.orbfin.api.gateway.services.ServiceToken;
//
//import reactor.core.publisher.Mono;
//
//import lombok.extern.slf4j.Slf4j;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
//
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import tech.orbfin.api.gateway.services.ServiceTokenJW;
//
//@Slf4j
//@RequiredArgsConstructor
//@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity
//@Configuration
//public class ConfigSecurity {
//    private final ConfigCORS configCORS;
//    private final ServiceAuthLogout serviceAuthLogot;
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                .addFilterAt(configCORS.corsWebFilter(), SecurityWebFiltersOrder.CORS)
//                .csrf(csrf -> csrf.disable())
//                .cors(corsSpec -> corsSpec.disable())
//                .httpBasic(httpBasic -> httpBasic.disable())
//                .authorizeExchange(authorizeExchange -> authorizeExchange
//                        .anyExchange().permitAll()
//                )
//                .logout(logout ->
//                        logout
//                                .logoutUrl("/logout")
//                                .logoutSuccessHandler(logoutSuccessHandler())
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public ServerLogoutSuccessHandler logoutSuccessHandler() {
//        return (exchange, authentication) -> {
//            SecurityContextHolder.clearContext();
//
//            try {
//                String accessToken = ServiceToken.getToken(exchange.getExchange());
//                String refreshToken = ServiceToken.getRefreshToken(exchange.getExchange());
//
//                serviceAuthLogot.logout(accessToken, refreshToken);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            return Mono.empty();
//        };
//    }
//}
