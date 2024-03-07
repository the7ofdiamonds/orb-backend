package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.model.Session;

import com.redis.om.spring.repository.RedisDocumentRepository;

import java.util.List;

public interface IRepositorySession extends RedisDocumentRepository<Session,String> {
    List<Session> findAll();
    Iterable<Session> findByAccessToken(String token);
    Iterable<Session> findByRefreshToken(String refreshToken);
    Iterable<Session> findByUsername(String username);
    Iterable<Session> findByRevokedTrue();
    void deleteById(String id);

}