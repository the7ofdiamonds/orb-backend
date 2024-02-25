package tech.orbfin.api.gateway.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ConfigKafka {

    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name(ConfigTopics.NEW_USER_SIGN_UP)
                .build();
    }
}