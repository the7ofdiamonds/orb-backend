package tech.orbfin.api.gateway.configurations;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import tech.orbfin.api.gateway.model.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@AllArgsConstructor
@EnableRedisRepositories
@Configuration
public class ConfigRedis {

//    @Bean
//    public ReactiveRedisOperations<String, Session> reactiveRedisOperations(
//            ReactiveRedisConnectionFactory connectionFactory) {
//
//        Jackson2JsonRedisSerializer<Session> jsonSerializer = new Jackson2JsonRedisSerializer<>(Session.class);
//        RedisSerializationContext<String, Session> serializationContext = RedisSerializationContext
//                .<String, Session>newSerializationContext(new StringRedisSerializer())
//                .key(new StringRedisSerializer())
//                .value(jsonSerializer)
//                .hashKey(new Jackson2JsonRedisSerializer<>(Object.class))
//                .hashValue(new GenericToStringSerializer<>(Object.class))
//                .build();
//
//        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
//    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory(config);
    }
}
