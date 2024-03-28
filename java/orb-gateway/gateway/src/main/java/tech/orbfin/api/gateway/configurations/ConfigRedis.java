package tech.orbfin.api.gateway.configurations;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRedisRepositories
@Configuration
public class ConfigRedis {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory(config);
    }
}
