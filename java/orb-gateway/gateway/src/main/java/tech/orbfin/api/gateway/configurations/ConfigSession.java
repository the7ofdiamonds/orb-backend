//package tech.orbfin.api.gateway.configurations;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
//import org.springframework.data.redis.core.ReactiveRedisOperations;
//import org.springframework.data.redis.core.ReactiveRedisTemplate;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
//import tech.orbfin.api.gateway.entities.token.Token;
//
//@Configuration
//public class ConfigSession {
//
//    @Bean
//    ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
//            ReactiveRedisConnectionFactory factory) {
//        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
//    }
//
//    @Bean
//    public ReactiveRedisSessionRepository reactiveRedisSessionRepository(
//            @Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
//        return new ReactiveRedisSessionRepository(reactiveRedisTemplate);
//    }
//}
