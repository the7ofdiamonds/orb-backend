package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.configurations.ConfigKafkaTopics;

import tech.orbfin.api.gateway.exceptions.*;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;

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

    public ResponseVerify verifyEmail(RequestVerify request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            User verifiedAccount = serviceUserUtils.verifyAccount(username, password, confirmationCode);

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

    public ResponseForgot forgotPassword(@NotNull RequestForgot request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

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
