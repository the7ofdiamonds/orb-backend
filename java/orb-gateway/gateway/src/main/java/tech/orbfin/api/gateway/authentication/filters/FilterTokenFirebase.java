package tech.orbfin.api.gateway.authentication.filters;

import lombok.RequiredArgsConstructor;
import tech.orbfin.api.gateway.services.ServiceSession;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.firebase.ServiceTokenFirebase;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@RequiredArgsConstructor
@Component
public class FilterTokenFirebase implements GlobalFilter {
    private final ServiceToken serviceToken;
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceSession serviceSession;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(String.valueOf(exchange.getRequest().getHeaders()));
        String jwt = ServiceToken.getToken(exchange);

        if (jwt == null) {
            log.info("Firebase Token could not be found in the header.");
            return chain.filter(exchange);
        }

        log.info("Validating token ...");

        boolean validatedToken;

        try {
            validatedToken = serviceTokenFirebase.verifyToken(jwt);
//  If valid return authenticated
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }

        if (!validatedToken) {
            log.info("Token is not valid");

            log.info("Searching for session to use Refresh Token ......");

            String refreshToken = ServiceToken.getRefreshToken(exchange);

            if (refreshToken == null) {
                return chain.filter(exchange);
            }

            boolean refreshTokenValid;

            try {
                refreshTokenValid = serviceTokenFirebase.verifyRefreshToken(refreshToken);
            } catch (FirebaseAuthException e) {
                throw new RuntimeException(e);
            }

            if (!refreshTokenValid) {
                return chain.filter(exchange);
            }

            String sessionRefreshToken = serviceSession.getRefreshToken(jwt);


            if (refreshToken.equals(sessionRefreshToken)) {
                boolean sessionRefreshTokenValid;

                try {
                    sessionRefreshTokenValid = serviceTokenFirebase.verifyRefreshToken(sessionRefreshToken);
                } catch (FirebaseAuthException e) {
                    throw new RuntimeException(e);
                }

                if (!sessionRefreshTokenValid) {
                    return chain.filter(exchange);
                }

                log.info("The refresh token is valid");

                log.info("Refresh Token : " + refreshToken);
            }

            log.info("The refresh token has been used to issue a new access token.");

            log.info("Access Granted");

        } else {
            log.info("Searching for session using validated token ......");

            log.info("Session has been located.");

            log.info("Access Granted");

        }

        return Mono.empty();
    }
}

