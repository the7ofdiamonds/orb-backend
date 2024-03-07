package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.model.Session;

import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.model.user.UserEntity;
import tech.orbfin.api.gateway.repositories.IRepositorySession;

import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Service
public class ServiceSession {
    private final IRepositorySession iRepositorySession;
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceUserUtils serviceUserUtils;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public boolean createSession(UserEntity user, String accessToken, String refreshToken) {
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

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

    public String getRefreshToken(String token) {
        Iterable<Session> sessions = iRepositorySession.findByToken(token);

        for (Session session : sessions) {
            return session.getRefreshToken();
        }

        return null;
    }

    public Iterable<Session> getAllTokens(String username) throws Exception {
        User user = serviceUserUtils.findUserByUsername(username);

        if (user == null) {
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

        Iterable<Session> sessions = iRepositorySession.findByToken(token);

        if (sessions == null) {
            return username;
        }

        for (Session session : sessions) {
            session.setRevoked(true);

            iRepositorySession.save(session);
        }

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
}
