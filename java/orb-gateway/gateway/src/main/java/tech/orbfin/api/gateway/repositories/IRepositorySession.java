package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.model.Session;

import com.redis.om.spring.repository.RedisDocumentRepository;

public interface IRepositorySession extends RedisDocumentRepository<Session,String> {
    Iterable<Session> findByToken(String token);
//    Iterable<Session> findByUserId(String token);
    Iterable<Session> findByRefreshToken(String refreshToken);
    Iterable<Session> findByUsername(String username);

}