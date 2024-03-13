package tech.orbfin.api.gateway.authentication.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.authentication.AuthJWT;

import tech.orbfin.api.gateway.model.session.Session;
import tech.orbfin.api.gateway.model.session.IRepositorySession;

import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserRoles;
import tech.orbfin.api.gateway.services.*;

import reactor.core.publisher.Mono;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;

import org.springframework.web.server.ServerWebExchange;
import tech.orbfin.api.gateway.utils.PHP;

@AllArgsConstructor
@Slf4j
@Component
public class FilterToken implements GlobalFilter {
    private final ServiceToken serviceToken;
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceUserUtils serviceUserUtils;
    private final ServiceUserDetails serviceUserDetails;
    private final IRepositorySession iRepositorySession;
    private final PHP php;
    private final IRepositoryUserRoles iRepositoryUserRoles;

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

            if (username.isEmpty()) {
                throw new Exception("This user does not exist please login again to gain access.");
            }

            var userRoles = iRepositoryUserRoles.getWPUserRoles();
            var userRolesUnserielized = php.unserialize(userRoles);
            log.info(userRolesUnserielized.toString());

            boolean tokenIsExpired = serviceTokenJW.isTokenExpired(jwt);

            if (tokenIsExpired) {
                log.info("Token is expired");

                String refreshToken = ServiceToken.getRefreshToken(exchange);

                Iterable<Session> sessions = iRepositorySession.findByRefreshToken(refreshToken);

                if (sessions == null) {
                    log.info("Unable to find token by Access Token ......");
                }

                boolean refreshTokenIsExpired = serviceTokenJW.isTokenExpired(refreshToken);

                if (refreshTokenIsExpired) {
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

            if (user != null) {
                AuthJWT authJWT = new AuthJWT(
                        true,
                        user.getUsername()
                );

                SecurityContextHolder.getContext().setAuthentication(authJWT);

                log.info("Valid token found for user: {}", user);
            }

            log.info(SecurityContextHolder.getContext().toString());

            return chain.filter(exchange);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return chain.filter(exchange);
    }
}
