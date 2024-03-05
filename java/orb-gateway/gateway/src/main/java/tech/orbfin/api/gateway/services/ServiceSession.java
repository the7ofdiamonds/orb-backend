package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.repositories.IRepositorySession;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ServiceSession {
    private final IRepositorySession iRepositorySession;

    public String getRefreshToken(String token){
        Iterable<Session> sessions = iRepositorySession.findByToken(token);

        for (Session session : sessions) {
            return session.getRefreshToken();
        }

         return null;
    }
}
