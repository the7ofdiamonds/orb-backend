package tech.orbfin.api.gateway.services;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.model.session.Session;

import tech.orbfin.api.gateway.model.wordpress.User;

import tech.orbfin.api.gateway.model.session.IRepositorySession;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUser;

import java.util.Collection;
import java.util.List;

@Slf4j
@Setter
@Getter
@Service
public class ServiceSession {
    private final IRepositorySession iRepositorySession;
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceUserUtils serviceUserUtils;
    private final IRepositoryUser iRepositoryUser;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Autowired
    public ServiceSession(IRepositorySession iRepositorySession, ServiceTokenJW serviceTokenJW, ServiceUserUtils serviceUserUtils, IRepositoryUser iRepositoryUser) {
        this.iRepositorySession = iRepositorySession;
        this.serviceTokenJW = serviceTokenJW;
        this.serviceUserUtils = serviceUserUtils;
        this.iRepositoryUser = iRepositoryUser;
    }

//    Needs work
    public boolean createSession(UserDetails user, String accessToken, String refreshToken) {
        String username = user.getUsername();
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) user.getAuthorities();

        boolean accountValid = serviceUserUtils.validateAccount(user);

        if (!accountValid) {
            return false;
        }

        long issued = System.currentTimeMillis();
        long expiration = System.currentTimeMillis() + refreshExpiration;

        Session session = new Session(serviceTokenJW.ALGORITHM, accessToken, refreshToken, username, authorities, issued, expiration, false);

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
