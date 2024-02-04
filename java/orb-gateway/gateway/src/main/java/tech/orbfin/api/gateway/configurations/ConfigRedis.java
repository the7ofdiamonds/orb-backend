package tech.orbfin.api.gateway.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;
import tech.orbfin.api.gateway.model.Session;
import tech.orbfin.api.gateway.model.token.Token;

@EnableRedisRepositories
@Configuration
public class ConfigRedis {

    @Bean
    public ReactiveRedisOperations<String, Session> reactiveRedisOperations(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Session> serializationContext = RedisSerializationContext
                .<String, Session>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(Session.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Token.class))
                .hashValue(new GenericToStringSerializer<>(Object.class))
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
