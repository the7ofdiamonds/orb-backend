package tech.orbfin.api.gateway.authentication.filters;

import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.authentication.AuthJWT;
import tech.orbfin.api.gateway.model.user.UserEntity;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceTokenJW;

import reactor.core.publisher.Mono;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Primary;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;

import tech.orbfin.api.gateway.services.ServiceUser;
import tech.orbfin.api.gateway.services.ServiceUserDetails;

@Primary
@Slf4j
@Component
@AllArgsConstructor
public class FilterJWT implements GlobalFilter {
    private final ServiceToken serviceToken;
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceUser serviceUser;
    private final ServiceUserDetails serviceUserDetails;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            log.info(String.valueOf(exchange.getRequest().getHeaders()));
            String jwt = serviceToken.getToken(exchange);

            if (jwt == null) {
                log.info("A Token could not be found in the header.");
                return chain.filter(exchange);
            }

            log.info("Validating token ...");

            String username = serviceTokenJW.extractUsername(jwt);

            log.info("Username {} is attempting to gain access to resource servers.", username);

            if (username.isEmpty()){
                throw new Exception("This user does not exist please login again to gain access.");
            }

            boolean tokenIsExpired = serviceTokenJW.isTokenExpired(jwt);

            if (tokenIsExpired) {
                log.info("Token is expired");

                String refreshToken = serviceToken.getRefreshToken(exchange);

                boolean refreshTokenIsExpired = serviceTokenJW.isTokenExpired(refreshToken);

                if(refreshTokenIsExpired){
                    log.info("Refresh Token is expired.");
                    return chain.filter(exchange);
                }

                log.info("Searching for session to use Refresh Token ......");

            } else {
                log.info("Searching for session using validated token ......");

                log.info("Session has been located.");

                log.info("Access Granted");

            }

            UserDetails user = serviceUserDetails.loadUserByUsername(username);

            if(user != null) {
                AuthJWT authJWT = new AuthJWT(
                        true,
                        user.getUsername()
                );

                SecurityContextHolder.getContext().setAuthentication(authJWT);

                log.info("Valid token found for user: {}", user);
            }

            log.info(SecurityContextHolder.getContext().toString());

            return chain.filter(exchange);
        } catch (Exception e){
            log.info(e.getMessage());
        }
        return chain.filter(exchange);
    }
}
