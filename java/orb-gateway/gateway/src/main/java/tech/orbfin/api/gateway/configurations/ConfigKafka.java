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
                .name(ConfigKafkaTopics.USER_REGISTER)
                .build();
    }

    @Bean
    public NewTopic passwordUpdate() {
        return TopicBuilder
                .name(ConfigKafkaTopics.PASSWORD_UPDATE)
                .build();
    }

    @Bean
    public NewTopic passwordChanged() {
        return TopicBuilder
                .name(ConfigKafkaTopics.PASSWORD_CHANGED)
                .build();
    }

    @Bean
    public NewTopic passwordForgot() {
        return TopicBuilder
                .name(ConfigKafkaTopics.PASSWORD_RECOVERY)
                .build();
    }
}