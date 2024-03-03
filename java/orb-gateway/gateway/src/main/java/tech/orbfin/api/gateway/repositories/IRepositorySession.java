package tech.orbfin.api.gateway.repositories;

import java.util.Set;

import org.springframework.stereotype.Repository;
import tech.orbfin.api.gateway.model.Session;

import com.redis.om.spring.repository.RedisDocumentRepository;

public interface IRepositorySession extends RedisDocumentRepository<Session,String> {
    Iterable<Session> findByToken(String token);
    Iterable<Session> findByRefreshToken(String refreshToken);
}