package tech.orbfin.api.gateway.authorization.filters;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.context.annotation.Primary;
import tech.orbfin.api.gateway.services.ServiceTokenJW;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Primary
@Slf4j
@Component
public class FilterJWT implements GlobalFilter {

    private final ServiceTokenJW serviceTokenJW;
    private final UserDetailsService userDetailsService;

    @Autowired
    public FilterJWT(ServiceTokenJW serviceTokenJW, UserDetailsService userDetailsService) {
        this.serviceTokenJW = serviceTokenJW;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (shouldSkipAuthentication(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);

        boolean tokenIsValid = serviceTokenJW.isTokenValid(jwt);

        if (!tokenIsValid) {
            return chain.filter(exchange);
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = serviceTokenJW.extractUsername(jwt);

            if (username == null) {
                return chain.filter(exchange);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        return chain.filter(exchange);
    }

    private boolean shouldSkipAuthentication(org.springframework.http.server.reactive.ServerHttpRequest request) {
        return "/".equals(request.getPath().toString());
    }
}
