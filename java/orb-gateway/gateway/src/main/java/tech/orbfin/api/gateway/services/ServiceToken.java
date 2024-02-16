package tech.orbfin.api.gateway.services;

import org.json.JSONException;
import org.json.JSONObject;
import tech.orbfin.api.gateway.repositories.RepositorySession;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
public class ServiceToken {
    private final RepositorySession repositorySession;

    public static String getToken(ServerWebExchange exchange){
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }

        return null;
    }

    public static String getTokenHeader(String jwt){
        String[] parts = jwt.split("\\.");

        if (parts.length == 3) {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[0]);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } else {
            System.err.println("Invalid JWT format");
        }

        return null;
    }

    public static String getTokenAlgo(String header) {
        if (header != null) {
            try {
                return new JSONObject(header).getString("alg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String getRefreshToken(ServerWebExchange exchange){
        String refreshToken = exchange.getRequest().getHeaders().getFirst("Refresh-Token");

        if(refreshToken != null){
            return refreshToken;
        }

        return null;
    }

    public Mono<Object> getRefreshTokenFromSession(Object jwt) {
        log.info("Get Refresh Token");

        return repositorySession.findByToken(jwt)
                .flatMap(session -> Mono.justOrEmpty(session.getRefreshToken()));
    }
}
