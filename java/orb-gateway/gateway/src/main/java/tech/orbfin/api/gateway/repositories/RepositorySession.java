//package tech.orbfin.api.gateway.repositories;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper; // Import ObjectMapper
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import tech.orbfin.api.gateway.model.Session;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.redis.om.spring.repository.RedisDocumentRepository;
//
//import org.springframework.data.redis.core.ReactiveRedisOperations;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Slf4j
//@Repository
//public class RepositorySession {
//    private final ReactiveRedisOperations<String, Session> operations;
//    private final Jackson2JsonRedisSerializer<Session> jsonRedisSerializer;
//    private final ObjectMapper objectMapper; // Inject ObjectMapper bean
//
//    @Autowired
//    public RepositorySession(
//            ReactiveRedisOperations<String, Session> operations,
//            ObjectMapper objectMapper) {
//        this.operations = operations;
//        this.jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Session.class);
//        this.objectMapper = objectMapper;
//    }
//
//    public Mono<Boolean> save(Session session) throws Exception {
//        String sessionId = session.getId().toString();
//        try {
////            String jsonSession = objectMapper.writeValueAsString(session);
//            return operations.opsForValue()
//                    .set(sessionId, session)
//                    .flatMap(success -> {
//                        if (success != null && success) {
//                            return Mono.just(true);
//                        } else {
//                            log.error("Failed to save session with id: {}", sessionId);
//                            return Mono.just(false);
//                        }
//                    })
//                    .onErrorResume(throwable -> {
//                        log.error("Error while saving session with id: {}", sessionId, throwable);
//                        return Mono.just(false);
//                    })
//                    .log();
//        } catch (Exception e) {
//            log.error("Error during serialization of session with id: {}", sessionId, e);
//            return Mono.just(false);
//        }
//    }
//
//
//    public Flux<Session> findAllValidSessionsByUserId(@Param("userId") Long userId) {
//        return operations.opsForHash()
//                .values(userId.toString())
//                .flatMap(sessionObjects ->
//                        Flux.fromIterable((Iterable<?>) sessionObjects)
//                                .filter(sessionObject -> sessionObject instanceof Session)
//                                .cast(Session.class)
//                )
//                .defaultIfEmpty(new Session());
//    }
//
//    // Find a session by token
//    public Mono<Session> findByToken(Object token) {
//        return operations.opsForValue()
//                .get(token)
//                .flatMap(Mono::just)
//                .defaultIfEmpty(new Session());
//    }
//
//    // Update a session
//    public Mono<Boolean> updateSession(Session session) {
//        return Mono.from(operations.opsForValue().set(session.getId().toString(), session));
//    }
//
//    public Mono<Boolean> deleteSession(String id) {
//        return Mono.from(operations.opsForValue().delete(id));
//    }
//}
