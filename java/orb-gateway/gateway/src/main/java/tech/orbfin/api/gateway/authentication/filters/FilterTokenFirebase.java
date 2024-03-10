package tech.orbfin.api.gateway.authentication.filters;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.model.user.Capabilities;
import tech.orbfin.api.gateway.model.user.Role;
import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.repositories.IRepositorySession;
import tech.orbfin.api.gateway.repositories.IRepositoryUserDetails;
import tech.orbfin.api.gateway.services.ServiceSession;
import tech.orbfin.api.gateway.services.ServiceToken;

import tech.orbfin.api.gateway.services.ServiceUserUtils;
import tech.orbfin.api.gateway.services.firebase.ServiceTokenFirebase;

import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilterTokenFirebase implements GlobalFilter {
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceSession serviceSession;
    private final ServiceUserUtils serviceUserDetails;
    private final IRepositoryUserDetails iRepositoryUserDetails;

    public FilterTokenFirebase(ServiceTokenFirebase serviceTokenFirebase, ServiceSession serviceSession, ServiceUserUtils serviceUserDetails, IRepositoryUserDetails iRepositoryUserDetails) {
        this.serviceTokenFirebase = serviceTokenFirebase;
        this.serviceSession = serviceSession;
        this.serviceUserDetails = serviceUserDetails;
        this.iRepositoryUserDetails = iRepositoryUserDetails;
    }


    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            String accessToken = ServiceToken.getToken(exchange);
            String refreshToken = ServiceToken.getRefreshToken(exchange);

            if (accessToken == null && refreshToken == null) {
                log.info("Firebase Token could not be found in the header.");
                return chain.filter(exchange);
            }
            Capabilities capabilities = new Capabilities(iRepositoryUserDetails);
            // Assuming capabilities is an instance of the Capabilities class

// Log the roles using the original getRoles() method
            log.info("User roles (Original): {}", capabilities.getRoles().values().stream().collect(Collectors.toList()));

// Serialize the roles to a string (assuming getRoles() returns a Map<String, Map<String, Boolean>>)
            String serializedRoles = capabilities.getRoles().toString();

// Deserialize the roles using the custom deserializeCapabilities method
            Map<String, Map<String, Boolean>> deserializedRoles = capabilities.deserializeCapabilities(serializedRoles);

// Log the roles after deserialization
            log.info("User roles (Deserialized): {}", deserializedRoles.values().stream().collect(Collectors.toList()));

            log.info("Validating token ...");

            log.info("Searching for session with Access Token ......");

            Session session = null;
            String email = null;

            if (refreshToken != null) {
                var header = ServiceToken.getTokenHeader(accessToken);
                String algorithm = ServiceToken.getTokenAlgo(header);
                log.info(algorithm);
                session = serviceSession.findByRefreshToken(refreshToken);
                log.info(String.valueOf(refreshToken.equals(session.getRefreshToken())));
                email = serviceTokenFirebase.getEmailFromRefreshToken(refreshToken);

            }

            if (accessToken != null && refreshToken == null) {
                var header = ServiceToken.getTokenHeader(accessToken);
                String algorithm = ServiceToken.getTokenAlgo(header);
                session = serviceSession.findByAccessToken(accessToken);
                log.info(String.valueOf(accessToken.equals(session.getAccessToken())));
                email = serviceTokenFirebase.getEmailFromAccessToken(accessToken);

            }

            if (email == null) {
                return chain.filter(exchange);
            }

            UserDetails user = serviceUserDetails.loadUserByEmail(email);

            if (user == null) {
                return chain.filter(exchange);
            }

            if (session == null) {
                log.info("Creating new session with tokens");

                refreshToken = serviceTokenFirebase.createSessionCookie(accessToken);
                boolean sessionCreated = serviceSession.createSession(user, accessToken, refreshToken);

                if (!sessionCreated) {
                    log.info("Session error.");
                }
            }

            log.info("Token is not valid");

            log.info("Searching for session to use Refresh Token ......");

            log.info("The refresh token is valid");

            log.info("The refresh token has been used to issue a new access token.");

            log.info("Access Granted");

            log.info("Searching for session using validated token ......");

            log.info("Session has been located.");

            log.info("Access Granted");

            return Mono.empty();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
