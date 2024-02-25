package tech.orbfin.api.gateway.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ConfigKafka {

    @Bean
    public NewTopic signup() {
        return TopicBuilder
                .name(ConfigTopics.USER_SIGN_UP)
                .build();
    }

    @Bean
    public NewTopic passwordChanged() {
        return TopicBuilder
                .name(ConfigTopics.PASSWORD_CHANGED)
                .build();
    }

    @Bean
    public NewTopic passwordForgot() {
        return TopicBuilder
                .name(ConfigTopics.PASSWORD_RECOVERY)
                .build();
    }
}