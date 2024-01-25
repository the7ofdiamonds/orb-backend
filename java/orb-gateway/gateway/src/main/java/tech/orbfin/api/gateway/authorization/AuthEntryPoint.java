package tech.orbfin.api.gateway.authorization;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import tech.orbfin.api.gateway.entities.user.UserEntity;
import tech.orbfin.api.gateway.services.ServiceTokenFirebase;
import tech.orbfin.api.gateway.services.ServiceTokenJW;
import tech.orbfin.api.gateway.services.ServiceUser;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Setter
@Getter
@Slf4j
@Component
@AllArgsConstructor
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {
    @Autowired
    private ServiceTokenJW serviceTokenJW;
    @Autowired
    private ServiceTokenFirebase serviceTokenFirebase;
    @Autowired
    private ServiceUser serviceUser;
    private Boolean tokenIsValid;
    private String username;
    private UserEntity user;

    public AuthEntryPoint(){}

//    public AuthEntryPoint(
//    ServiceTokenJW serviceTokenJW,
//    ServiceTokenFirebase serviceTokenFirebase,
//    ServiceUser serviceUser){
//        this.serviceTokenJW = serviceTokenJW;
//        this.serviceTokenFirebase = serviceTokenFirebase;
//        this.serviceUser = serviceUser;
//    }

    public AuthEntryPoint(String username){
        this.username = username;
    }

    private String getToken(ServerWebExchange exchange){
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return null;
    }

    private int countOccurrences(String text, char searchChar) {
        int count = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == searchChar) {
                count++;
            }
        }

        return count;
    }

    public String setAlgorithm(String token) {
        String[] section = token.split(".");

//        var dot = ".".charAt(0);
//        int count = countOccurrences(token, dot);

        String tokenSignature = "";

        if(section.length == 3) {
            tokenSignature = section[2];
        }

        byte[] utf8Bytes = tokenSignature.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        int sizeInBytes = utf8Bytes.length;

        log.info(String.valueOf(sizeInBytes));

        if(sizeInBytes == 32){
            return "HS256";
        }

//        if(sizeInBytes >= 256){
            return "RS256";
//        }

//        return "unknown";
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        try {
            log.info("Authentication entry point commence");
            String token = getToken(exchange);

            if (token == null) throw new Exception("A Token could not be found.");

            String algorithm = setAlgorithm(token);

            log.info("Token is valid. Extracting username...");

            log.info(algorithm);

            UserEntity user = null;

            if(algorithm.equals("HS256")){
                if(serviceTokenJW.isTokenValid(token)) {
                    var username = serviceTokenJW.extractUsername(token);
                    user = serviceUser.loadUserByUsername(username);
                }
            }

            if(algorithm.equals("RS256")){
                if(serviceTokenFirebase.verifyIdToken(token)){
                    user = serviceTokenFirebase.getUser(token);
                }
            }

            if(algorithm.equals("unknown")){
                throw new Exception("Could not find an algorithm for this token.");
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
