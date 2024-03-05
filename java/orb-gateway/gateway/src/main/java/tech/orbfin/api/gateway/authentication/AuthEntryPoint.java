package tech.orbfin.api.gateway.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.services.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import tech.orbfin.api.gateway.services.firebase.ServiceTokenFirebase;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
@Component
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    private final ServiceToken serviceToken;
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceUserFirebase serviceUserFirebase;
    private final ServiceUserDetails serviceUserDetails;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        try {
            log.info("Authentication entry point commence");
            String token = ServiceToken.getToken(exchange);

            if (token == null) throw new Exception("A Token could not be found.");

            String header = ServiceToken.getTokenHeader(token);
            String algo = ServiceToken.getTokenAlgo(header);

            log.info("Token is valid. Extracting username...");

            UserDetails user = null;

            if (algo.equals("HS256")){
                boolean tokenExpired = serviceTokenJW.isTokenExpired(token);

                if(tokenExpired) {
                    log.info("Token is expired.");
                }

                log.info("Token is valid");

                String username = serviceTokenJW.extractUsername(token);
                user = serviceUserDetails.loadUserByUsername(username);
            }

            if (algo.equals("RS256")) {
                var firebaseToken = serviceTokenFirebase.verifyToken(token);

                if (firebaseToken == null) {
                    log.info("IDToken is not valid.");
                }

                log.info("IDToken is valid");
                FirebaseToken verifiedToken = FirebaseAuth.getInstance(FirebaseApp.getInstance("ORB")).verifyIdToken(token, true);

                UserRecord firebaseUser = serviceUserFirebase.getUser(verifiedToken.getUid());
                user = serviceUserDetails.loadUserByEmail(firebaseUser.getEmail());
            }

            log.info("Setting authentication in SecurityContextHolder...");

            if(user != null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                log.info("Valid token found for user: {}", user);
            }

            return exchange.getResponse().setComplete();
        } catch (Exception e) {
            log.error("An error occurred in the authentication entry point: " + e.getMessage());
            return Mono.empty();
        }
    }
}
