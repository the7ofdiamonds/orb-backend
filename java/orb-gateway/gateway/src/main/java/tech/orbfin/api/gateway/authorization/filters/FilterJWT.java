package tech.orbfin.api.gateway.authorization.filters;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.services.ServiceToken;
import tech.orbfin.api.gateway.services.ServiceTokenJW;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.security.core.context.SecurityContextHolder;

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
    private final RepositoryUser repositoryUser;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            log.info("Filter JWT is being used");
            log.info(String.valueOf(exchange.getRequest().getHeaders()));

            String jwt = serviceToken.getToken(exchange);

            if (jwt == null) {
                log.info("A Token could not be found in the header.");
                return chain.filter(exchange);
            }

            log.info("Validating token ...");

            boolean tokenIsExpired = serviceTokenJW.isTokenExpired(jwt);

            if (tokenIsExpired) {
                log.info("Token is expired");

                String username = serviceTokenJW.extractUsername(jwt);

                if (username.isEmpty()){
                    throw new Exception("This user does not exist please login again to gain access.");
                }

                log.info("Searching for session to use Refresh Token ......");

//                return serviceToken.getRefreshToken(jwt)
//                        .map(rToken -> {
//                            boolean isExpired = serviceTokenJW.isTokenExpired((String) rToken);
//
//                            if(isExpired == true){
//                                return;
//                            }
//
//                            String uname = serviceTokenJW.extractUsername((String) rToken);
//
//                            Optional<UserEntity> usr = repositoryUser.findByUsername(uname);
//
//                            if(usr.isEmpty()){
//                                return;
//                            }
//                            log.info("Refresh Token: " + rToken);
//                            return rToken;
//                        });
            } else {
                log.info("Searching for session using validated token ......");

                log.info("Session has been located.");

                log.info("Access Granted");

            }




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
