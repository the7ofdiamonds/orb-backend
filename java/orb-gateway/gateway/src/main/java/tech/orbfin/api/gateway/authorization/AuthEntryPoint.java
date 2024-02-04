package tech.orbfin.api.gateway.authorization;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import tech.orbfin.api.gateway.model.user.UserEntity;
import tech.orbfin.api.gateway.services.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import static org.apache.commons.codec.binary.Base64.decodeBase64;

@Setter
@Getter
@Slf4j
@Component
@NoArgsConstructor
@AllArgsConstructor
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    @Autowired
    private ServiceToken serviceToken;
    @Autowired
    private ServiceTokenJW serviceTokenJW;
    @Autowired
    private ServiceTokenFirebase serviceTokenFirebase;
    @Autowired
    private ServiceUserFirebase serviceUserFirebase;
    @Autowired
    private ServiceUser serviceUser;
    private Boolean tokenIsValid;
    private String username;
    private UserEntity user;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        try {
            log.info("Authentication entry point commence");
            String token = serviceToken.getToken(exchange);
log.info(token);
            if (token == null) throw new Exception("A Token could not be found.");

            log.info("Token is valid. Extracting username...");

            var firebaseToken = serviceTokenFirebase.verifyToken(token);

            if(firebaseToken != null){
                log.info("Idtoken is valid");
                FirebaseToken verifiedToken = FirebaseAuth.getInstance(FirebaseApp.getInstance("ORB")).verifyIdToken(token, true);

                UserRecord user = serviceUserFirebase.getUser(verifiedToken.getUid());
            }

            log.info("Setting authentication in SecurityContextHolder...");

            log.info(String.valueOf(user));

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
