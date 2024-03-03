package tech.orbfin.api.gateway.services;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.repositories.IRepositorySession;

@RequiredArgsConstructor
@Service
public class ServiceSession {
    private final IRepositorySession iRepositorySession;

    public Session findByToken(String token) {
        try {
            Iterable<Session> sessions = iRepositorySession.findByToken(token);

            for (Session session : sessions) {
                return Session.builder()
                        .id(session.getId())
                        .token(session.getToken())
                        .refreshToken(session.getRefreshToken())
                        .algorithm(session.getAlgorithm())
                        .expired(session.isExpired())
                        .revoked(session.isRevoked())
                        .isAuthenticated(session.isAuthenticated())
                        .build();
            }

            return null;
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return null;
        }
    }

}
