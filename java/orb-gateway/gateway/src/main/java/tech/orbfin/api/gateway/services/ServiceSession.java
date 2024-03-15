package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.authentication.AuthJWT;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.session.Session;
import tech.orbfin.api.gateway.model.session.IRepositorySession;

import tech.orbfin.api.gateway.model.wordpress.User;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUser;

import java.util.Collection;
import java.util.List;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Autowired
    public ServiceSession(IRepositorySession iRepositorySession, ServiceTokenJW serviceTokenJW, ServiceUserUtils serviceUserUtils, ServiceUserDetails serviceUserDetails, IRepositoryUser iRepositoryUser) {
        this.iRepositorySession = iRepositorySession;
        this.serviceTokenJW = serviceTokenJW;
        this.serviceUserUtils = serviceUserUtils;
        this.serviceUserDetails = serviceUserDetails;
        this.iRepositoryUser = iRepositoryUser;
    }

//    Needs work
    public boolean createSession(String username, String accessToken, String refreshToken) {

        if (accessToken == null) {
            log.info("A Token could not be found in the header.");
        }

        log.info("Validating token ...");

        log.info("Username {} is attempting to gain access to resource servers.", username);

        var user = serviceUserDetails.loadUserByUsername(username);

        boolean tokenIsExpired = serviceTokenJW.isTokenExpired(accessToken);

        if (tokenIsExpired) {
            log.info("Token is expired");

            Iterable<Session> sessions = iRepositorySession.findByRefreshToken(refreshToken);

            if (sessions == null) {
                log.info("Unable to find token by Access Token ......");
            }

            boolean refreshTokenIsExpired = serviceTokenJW.isTokenExpired(refreshToken);

            if (refreshTokenIsExpired) {
                log.info("Refresh Token is expired.");
            }

            log.info("Searching for session to use Refresh Token ......");

        } else {
            log.info("Searching for session using validated token ......");

            log.info("Session has been located.");

            log.info("Access Granted");

        }


//        if (user != null) {
//            AuthJWT authJWT = new AuthJWT(
//                    true,
//                    user.getUsername()
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authJWT);
//
//            log.info("Valid token found for user: {}", user);
//        }

        log.info(SecurityContextHolder.getContext().toString());



//        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();

        boolean accountValid = serviceUserUtils.validateAccount(user);

        if (!accountValid) {
            return false;
        }

        var authorities = user.getAuthorities();
        long issued = System.currentTimeMillis();
        long expiration = System.currentTimeMillis() + refreshExpiration;

        Session session = new Session(serviceTokenJW.ALGORITHM, accessToken, refreshToken, username, (Collection<GrantedAuthority>) authorities, issued, expiration, false);

        iRepositorySession.save(session);

        return true;
    }

    public Session findByAccessToken(String token) throws Exception {
        log.info("Find by access token called.");

        List<Session> sessions = iRepositorySession.findAll();

        for (Session session : sessions) {
            if (token.equals(session.getAccessToken())) {
                return session;
            }
        }

        return null;
    }

    public Session findByRefreshToken(String token) throws Exception {
        log.info("Find by refresh token called.");

        List<Session> sessions = iRepositorySession.findAll();

        for (Session session : sessions) {
            if (token.equals(session.getRefreshToken())) {
                return session;
            }
        }

        return null;
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
        String username = serviceTokenJW.extractUsername(token);

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

    public boolean deleteAllRevokedSessions() throws Exception {
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
