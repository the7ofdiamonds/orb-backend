package tech.orbfin.api.gateway.services.admin;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.response.ResponseDeleteAccount;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryAdmin;
import tech.orbfin.api.gateway.services.user.ServiceUserDetails;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceAdmin {
    private final IRepositoryAdmin iRepositoryAdmin;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserDetails serviceUserDetails;

    public ResponseDeleteAccount deleteAccount(String email, String username, String confirmationCode) throws Exception {
        var userEntity = serviceUserDetails.loadUserByUsername(username);

        boolean accountEnabled = userEntity.isEnabled();

        if (accountEnabled) {
            throw new Exception(ExceptionMessages.ACCOUNT_DELETE_ERROR);
        }

        boolean accountDeleted = iRepositoryAdmin.deleteAccount(email, username, confirmationCode);

        if (!accountDeleted) {
            throw new Exception(ExceptionMessages.ACCOUNT_DELETE_ERROR);
        }

        kafkaTemplate.send(ConfigKafkaTopics.ACCOUNT_DELETED, email);

        return new ResponseDeleteAccount(email);
    }
}
