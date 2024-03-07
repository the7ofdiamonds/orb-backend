package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.user.UserEntity;
import tech.orbfin.api.gateway.model.user.User;

import tech.orbfin.api.gateway.model.request.RequestLogin;
import tech.orbfin.api.gateway.model.response.ResponseLogin;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.jetbrains.annotations.NotNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceAuthLogin {
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceSession serviceSession;
    private final ServiceUserUtils serviceUserUtils;

    public ResponseLogin login(@NotNull RequestLogin request) throws Exception {
        try {
            log.info("Login function has been called.");

            var username = request.getUsername();
            var password = request.getPassword();
            Object location = request.getLocation();

            log.info("User {} is attempting to login", username);

            User validAccount = serviceUserUtils.validateCredentials(username, password);

            if(validAccount == null){
               throw new BadCredentialsException(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            String email = validAccount.getEmail();
            UserEntity userEntity = new UserEntity(validAccount);

            username = validAccount.getUsername();
            var role = validAccount.getRoles();

            log.info("{} {} is attempting to login.", role, username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            log.info("Username {} is recorded in the Firebase Users Database with the email {}.", username, email);

            String accessToken = serviceTokenJW.generateToken(extraClaims, userEntity);
            String refreshToken = serviceTokenJW.refreshToken(userEntity);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            boolean sessionCreated = serviceSession.createSession(userEntity, accessToken, refreshToken);

            if(!sessionCreated){
                throw new Exception(ExceptionMessages.SESSION_CREATE_ERROR);
            }

            log.info("Session created successfully for {}", username);

            return new ResponseLogin(username, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.LOGIN_ATTEMPT_ERROR + e.getMessage());
        }
    }
}