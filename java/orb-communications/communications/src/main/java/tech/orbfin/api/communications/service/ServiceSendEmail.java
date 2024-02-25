package tech.orbfin.api.communications.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tech.orbfin.api.communications.configurations.ConfigTopics;

@Service
public class ServiceSendEmail {

    @KafkaListener(topics = ConfigTopics.NEW_USER_SIGN_UP, groupId = "user-group")
    public void signUp(String user) {
        System.out.println(user);
    }
}
