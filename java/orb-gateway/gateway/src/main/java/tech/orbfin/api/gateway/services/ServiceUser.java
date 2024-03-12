package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.*;

import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.wordpress.User;

import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUser;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ServiceUser {
    private final IRepositoryUser iRepositoryUser;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ServiceUserUtils serviceUserUtils;
    private final ServiceUserAccount serviceUserAccount;

    public ResponseVerify verifyEmail(String username, String password, String confirmationCode) throws Exception {
        try {
            User verifiedAccount = serviceUserAccount.verifyAccount(username, password, confirmationCode);

            if (verifiedAccount == null) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            boolean accountEnabled = verifiedAccount.getIsEnabled();

            if (!accountEnabled) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            return new ResponseVerify("email", verifiedAccount.getEmail());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.EMAIL_VERIFIED_ERROR + e.getMessage());
        }
    }

    public ResponseForgot forgotPassword(String email, String username) throws Exception {
        try {
            User user;

            if (email == null && username == null) {
                throw new BadCredentialsException(ExceptionMessages.PASSWORD_CHANGE_NULL);
            }

            if (email == null) {
                user = serviceUserUtils.findUserByUsername(username);
                email = user.getEmail();
            }

            user = serviceUserUtils.findUserByEmail(email);

            if (username != null && user == null) {
                boolean userExistByUsername = iRepositoryUser.existsByUsername(username);

                if (!userExistByUsername) {
                    throw new BadCredentialsException(ExceptionMessages.USER_NOT_FOUND);
                }

                user = serviceUserUtils.findUserByUsername(username);
            }

            if (user == null) {
                throw new UserNotFoundException();
            }

            email = user.getEmail();

            kafkaTemplate.send(ConfigKafkaTopics.PASSWORD_RECOVERY, email);

            return new ResponseForgot(email);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.PASSWORD_CHANGE_ERROR + e.getMessage());
        }
    }
}
