package tech.orbfin.api.gateway.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import tech.orbfin.api.gateway.entities.Session;

@Configuration
@EnableRedisWebSession
public class ConfigRedis {

    @Bean
    public ReactiveRedisTemplate<String, Session> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Session> serializationContext =
                RedisSerializationContext.<String, Session>newSerializationContext(new StringRedisSerializer())
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new Jackson2JsonRedisSerializer<>(Session.class))
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}
