package tech.orbfin.api.gateway.authorization.filters;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceTokenFirebase;
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
@AllArgsConstructor
public class FilterJWT implements GlobalFilter {
    private final ServiceToken serviceToken;
    private final ServiceTokenJW serviceTokenJW;
    private final UserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            log.info("Filter JWT is being used");
            log.info(String.valueOf(exchange.getRequest().getHeaders()));

            String jwt = serviceToken.getToken(exchange);

            if (jwt == null) {
                return chain.filter(exchange);
            }

            log.info(jwt);

            boolean tokenIsValid = serviceTokenJW.isTokenExpired(jwt);

            if (!tokenIsValid) {
                log.info("Token is not valid");
                return chain.filter(exchange);
            }

//            if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                String username = serviceTokenJW.extractUsername(jwt);

//                if (username == null) {
//                    return chain.filter(exchange);
//                }

//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities()
//                );

//                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info(SecurityContextHolder.getContext().toString());
//            return securityContextRepository.save(exchange, SecurityContextHolder.getContext());
//            }

            return chain.filter(exchange);
        } catch (Exception e){
            log.info(e.getMessage());
        }
        return chain.filter(exchange);
    }
}
