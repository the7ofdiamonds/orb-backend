package tech.orbfin.api.gateway.repositories;

import tech.orbfin.api.gateway.model.Session;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RepositorySession {
    private final ReactiveRedisOperations<String, Session> operations;

    @Autowired
    public RepositorySession(ReactiveRedisOperations<String, Session> operations) {
        this.operations = operations;
    }

    public Mono<Boolean> save(Session session) {
        return operations.opsForValue()
                .set(session.getId().toString(), session)
                .flatMap(success -> Mono.just(success ? true : false))
                .log();
    }

    public Flux<Session> findAllValidSessionsByUserId(@Param("userId") Long userId) {
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
    public Mono<Session> findByToken(Object token) {
        return operations.opsForValue()
                .get(token)
                .flatMap(Mono::just)
                .defaultIfEmpty(new Session());
    }

    // Update a session
    public Mono<Boolean> updateSession(Session session) {
        return Mono.from(operations.opsForValue().set(session.getId().toString(), session));
    }

    // Delete a session by ID
    public Mono<Boolean> deleteSession(String id) {
        return Mono.from(operations.opsForValue().delete(id));
    }
}
