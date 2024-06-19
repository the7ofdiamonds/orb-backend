package tech.orbfin.api.gateway.services.session;

import org.springframework.web.server.ServerWebExchange;
import tech.orbfin.api.gateway.services.user.ServiceUserDetails;
import tech.orbfin.api.gateway.services.user.ServiceUserUtils;
import tech.orbfin.api.gateway.services.authentication.AuthJWT;

import tech.orbfin.api.gateway.services.authentication.Authenticated;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.session.Session;
import tech.orbfin.api.gateway.model.session.IRepositorySession;

import tech.orbfin.api.gateway.model.wordpress.User;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.services.firebase.ServiceTokenFirebase;
import tech.orbfin.api.gateway.services.token.ServiceToken;
import tech.orbfin.api.gateway.services.token.ServiceTokenJW;

@Slf4j
@Setter
@Getter
@Service
public class ServiceSession {
    private final IRepositorySession iRepositorySession;
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceUserUtils serviceUserUtils;
    private final ServiceUserDetails serviceUserDetails;
    private final IRepositoryUser iRepositoryUser;
    private final ServiceTokenFirebase serviceTokenFirebase;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Autowired
    public ServiceSession(IRepositorySession iRepositorySession, ServiceTokenJW serviceTokenJW, ServiceUserUtils serviceUserUtils, ServiceUserDetails serviceUserDetails, IRepositoryUser iRepositoryUser, ServiceTokenFirebase serviceTokenFirebase) {
        this.iRepositorySession = iRepositorySession;
        this.serviceTokenJW = serviceTokenJW;
        this.serviceUserUtils = serviceUserUtils;
        this.serviceUserDetails = serviceUserDetails;
        this.iRepositoryUser = iRepositoryUser;
        this.serviceTokenFirebase = serviceTokenFirebase;
    }

    //    Needs work
    public boolean createSession(Session session) {
        if (session.getUsername() == null) {
            log.info("A user is required.");
        }

        if (session.getAccessToken() == null) {
            log.info("A Token could not be found in the header.");
        }

        if (session.getRefreshToken() == null) {
            log.info("A Refresh Token could not be found in the header.");
        }

        log.info("Validating token ...");
        log.info(session.getUsername());
        UserDetails user = serviceUserDetails.loadUserByUsername(session.getUsername());

        log.info("Username {} is attempting to gain access to resource servers.", session.getUsername());

        boolean accountValid = serviceUserUtils.validateAccount(user);

        if (!accountValid) {
            return false;
        }

        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();

        log.info("Searching for session using validated token ......");

        log.info("Session has been located.");

        log.info("Access Granted");

        AuthJWT authJWT = new AuthJWT(
                true,
                user.getUsername()
        );

        SecurityContextHolder.getContext().setAuthentication(authJWT);

        iRepositorySession.save(session);

        return true;
    }

    private boolean updateSession(String sessionID, UserDetails user, String accessToken, String refreshToken) {
        if (user == null) {
            log.info("A user is required.");
        }

        if (accessToken == null) {
            log.info("A Token could not be found in the header.");
        }

        if (refreshToken == null) {
            log.info("A Refresh Token could not be found in the header.");
        }

        log.info("Validating token ...");

        boolean accountValid = serviceUserUtils.validateAccount(user);

        if (!accountValid) {
            return false;
        }

        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();

        log.info("Searching for session using validated token ......");

        log.info("Session has been located.");

        log.info("Access Granted");

        AuthJWT authJWT = new AuthJWT(
                true,
                user.getUsername()
        );

        SecurityContextHolder.getContext().setAuthentication(authJWT);

        Authenticated authenticated = new Authenticated("171", "fakeuser@gmail.com", "fakeuser-2", accessToken, refreshToken);
        Session session = new Session(authenticated);

        iRepositorySession.update(session);

        return true;
    }

    public boolean validateSession(ServerWebExchange exchange) {
        String accessToken = ServiceToken.getTokenFromExchange(exchange);

        if (accessToken == null) {
            log.info("Access Token is required");
            return false;
        }

        String refreshToken = ServiceToken.getRefreshToken(exchange);

        if (refreshToken == null) {
            log.info("Refresh Token is required");
            return false;
        }

        String header = ServiceToken.getTokenHeader(accessToken);
        String algo = ServiceToken.getTokenAlgo(header);

        if (algo == null) {
            log.info("Algo is required");
            return false;
        }

        UserDetails validUser = null;

        if (algo.equals("HS256")) {
            validUser = serviceTokenJW.getValidUserFromAccessToken(accessToken);
        }

        if (algo.equals("RS256")) {
            validUser = serviceTokenFirebase.getValidUserFromAccessToken(accessToken);
        }

        if (validUser == null) {
            log.info("User is not valid");
            return false;
        }

        Session session = iRepositorySession.findByRefreshToken(refreshToken);

        if (session == null) {
            log.info("Unable to find session by Refresh Token.");
            return false;
        }

        if (System.currentTimeMillis() > session.getExpiration()) {
            log.info("Session is expired.");
            return false;
        }

        if (session.isRevoked()) {
            log.info("Session is revoked.");
            return false;
        }

        var longitude = "here";
        var latitude = "there";

        Map<String, String> location = new HashMap<>();
        location.put("longitude", longitude);
        location.put("latitude", latitude);

        Map<String, Object> extraClaims = new HashMap<>();

        extraClaims.put("location", location);

        String newAccessToken = ServiceTokenJW.generateToken(extraClaims, validUser);

        log.info("New Access Token issued to username: {}", validUser.getUsername());

        boolean sessionUpdated = updateSession(session.getId(), validUser, newAccessToken, refreshToken);

        if (!sessionUpdated) {
            log.info("Session could not be updated.");
            return false;
        }

        return true;
    }

    public Iterable<Session> getAllTokens(String username) throws Exception {
        boolean user = iRepositoryUser.existsByUsername(username);

        if (!user) {
            throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_FOUND);
        }

        Iterable<Session> sessions = iRepositorySession.findByUsername(username);

        if (sessions == null) {
            return null;
        }

        return sessions;
    }

    public String removeSession(String token) throws Exception {
        String username = ServiceTokenJW.extractUsername(token);

        if (username == null) {
            throw new BadCredentialsException(ExceptionMessages.USERNAME_NULL);
        }

        User user = serviceUserUtils.findUserByUsername(username);

        if (user == null) {
            throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_FOUND);
        }

        Session session = iRepositorySession.findByAccessToken(token);

        session.setRevoked(true);

        iRepositorySession.save(session);

        return username;
    }

    public boolean removeAllSessions(String username) throws Exception {
        Iterable<Session> sessions = getAllTokens(username);

        if (sessions == null) {
            return true;
        }

        for (Session session : sessions) {
            session.setRevoked(true);

            iRepositorySession.save(session);
        }

        return true;
    }

    public boolean deleteAllRevokedSessions() {
        List<Session> sessions = iRepositorySession.findAll();

        if (sessions == null) {
            return true;
        }

        for (Session session : sessions) {
            boolean revoked = session.isRevoked();

            if (revoked) {
                iRepositorySession.deleteById(session.getId());
            }
        }

        return true;
    }
}
