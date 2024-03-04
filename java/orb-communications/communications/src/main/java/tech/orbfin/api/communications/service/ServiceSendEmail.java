package tech.orbfin.api.communications.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tech.orbfin.api.communications.configurations.ConfigTopics;

@Service
public class ServiceSendEmail {

    @KafkaListener(topics = ConfigTopics.USER_SIGN_UP, groupId = "user-group")
    public void signUp(String user) { System.out.println("Confirmation email sent to " + user); }

    @KafkaListener(topics = ConfigTopics.USERNAME_CHANGED, groupId = "user-group")
    public void usernameChanged(String user) {
        System.out.println("Username changed email was sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.PASSWORD_UPDATE, groupId = "user-group")
    public void passwordUpdate(String user) {
        System.out.println("Password update email was sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.PASSWORD_CHANGED, groupId = "user-group")
    public void passwordChanged(String user) {
        System.out.println("Password changed email was sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.PASSWORD_RECOVERY, groupId = "user-group")
    public void passwordForgot(String user) {
        System.out.println("Password recovery email sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.NAME_CHANGED, groupId = "user-group")
    public void nameChanged(String user) {
        System.out.println("Name changed email was sent to " + user);
    }

    @KafkaListener(topics = ConfigTopics.PHONE_CHANGED, groupId = "user-group")
    public void phoneChanged(String user) {
        System.out.println("Phone changed email was sent to " + user);
    }
}
