package tech.orbfin.api.gateway.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

@Service
@Getter
@RequiredArgsConstructor
public class ServiceToken {

    public String getToken(ServerWebExchange exchange){
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }

        return null;
    }
}
