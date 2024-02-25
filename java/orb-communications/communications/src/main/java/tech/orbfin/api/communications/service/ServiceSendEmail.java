package tech.orbfin.api.communications.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tech.orbfin.api.communications.configurations.ConfigTopics;

@Service
public class ServiceSendEmail {

    @KafkaListener(topics = ConfigTopics.USER_SIGN_UP, groupId = "user-group")
    public void signUp(String user) {
        System.out.println("Confirmation email sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.PASSWORD_CHANGED, groupId = "user-group")
    public void passwordChanged(String user) {
        System.out.println("Password changed email was sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.PASSWORD_RECOVERY, groupId = "user-group")
    public void passwordForgot(String user) {
        System.out.println("Password recovery email sent to " + user);
    }
}
