package tech.orbfin.api.gateway.services;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.model.response.ResponseLogout;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositorySession;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceAuthLogout {
    private final ServiceUser serviceUser;
    private final ServiceTokenJW serviceTokenJW;
    private final IRepositorySession iRepositorySession;

    @Transactional
    public ResponseLogout logout(String token) {
        try {
            log.info("Service Auth Logout");

            String username = serviceTokenJW.extractUsername(token);
            User user = serviceUser.findUserByUsername(username);

            if (user == null) {
                return ResponseLogout.builder()
                        .errorMessage("The username " + username + " can not be found.")
                        .build();
            }

            Iterable<Session> sessions = iRepositorySession.findByToken(token);

            if (sessions == null) {
                SecurityContextHolder.clearContext();
            }

            for (Session session : sessions) {
                session.setAuthenticated(false);
                session.setExpired(true);
                session.setRevoked(true);
                iRepositorySession.save(session);
            }
            log.info(username);
            return new ResponseLogout(username);
        } catch (Exception e) {
            return ResponseLogout.builder()
                    .errorMessage("Internal server error: " + e.getMessage())
                    .build();
        }
    }
}
