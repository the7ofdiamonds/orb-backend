package tech.orbfin.api.gateway.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.response.ResponseDeleteAccount;
import tech.orbfin.api.gateway.repositories.IRepositoryUserAccount;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceAdmin {
    private final IRepositoryUserAccount iRepositoryUserAccount;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserDetails serviceUserDetails;

    public ResponseDeleteAccount deleteAccount(String email, String username) throws Exception {
        var userEntity = serviceUserDetails.loadUserByUsername(username);

        boolean accountEnabled = userEntity.isEnabled();

        if (accountEnabled) {
            throw new Exception(ExceptionMessages.ACCOUNT_DELETE_ERROR);
        }

        boolean accountDeleted = iRepositoryUserAccount.deleteAccount(email, username);

        if (!accountDeleted) {
            throw new Exception(ExceptionMessages.ACCOUNT_DELETE_ERROR);
        }

        kafkaTemplate.send(ConfigKafkaTopics.ACCOUNT_DELETED, email);

        return new ResponseDeleteAccount(email);
    }
}
