package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.entities.Session;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class RepositorySession {
    private final ReactiveRedisOperations<String, Session> operations;

    public RepositorySession(ReactiveRedisOperations<String, Session> operations) {
        this.operations = operations;
    }

    public Mono<Boolean> save(Session session) {
        return Mono.from(operations.opsForValue().set(session.getId().toString(), session));
    }

    // Find all valid sessions by user ID
    public Flux<Session> findAllValidSessionsByUserId(@Param("userId") String userId) {
        return operations.opsForHash()
                .values(userId.toString())
                .flatMap(sessionObjects ->
                        Flux.fromIterable((Iterable<?>) sessionObjects)
                                .filter(sessionObject -> sessionObject instanceof Session)
                                .cast(Session.class)
                )
                .defaultIfEmpty(new Session());  // Provide a default Session if there are no sessions
    }

    // Find a session by token
    public Mono<Session> findByToken(String token) {
        return Mono.from(operations.opsForValue().get(token));
    }

    // Update a session
    public Mono<Boolean> updateSession(Session session) {
        return Mono.from(operations.opsForValue().set(session.getId(), session));
    }

    // Delete a session by ID
    public Mono<Boolean> deleteSession(String id) {
        return Mono.from(operations.opsForValue().delete(id));
    }
}
