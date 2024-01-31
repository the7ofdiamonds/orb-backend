package tech.orbfin.api.gateway.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.*;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.MapSession;
import org.springframework.session.config.ReactiveSessionRepositoryCustomizer;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.session.data.redis.RedisSessionMapper;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import reactor.core.publisher.Mono;
import tech.orbfin.api.gateway.entities.Session;
import tech.orbfin.api.gateway.entities.token.Token;

import java.time.Duration;
import java.util.Map;
import java.util.function.BiFunction;

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
