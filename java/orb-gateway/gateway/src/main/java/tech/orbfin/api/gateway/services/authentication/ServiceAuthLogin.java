package tech.orbfin.api.gateway.services.authentication;

import tech.orbfin.api.gateway.services.session.ServiceSession;
import tech.orbfin.api.gateway.services.token.ServiceTokenJW;
import tech.orbfin.api.gateway.services.user.ServiceUserDetails;
import tech.orbfin.api.gateway.services.user.ServiceUserUtils;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.response.ResponseLogin;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import tech.orbfin.api.gateway.model.session.Session;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceAuthLogin {
    private final ServiceTokenJW serviceTokenJW;
    private final ServiceSession serviceSession;
    private final ServiceUserUtils serviceUserUtils;
    private final ServiceUserDetails serviceUserDetails;

    public ResponseLogin login(String ip, String userAgent, String username, String password, Object location) throws Exception {
        try {
            log.info("Login function has been called from {}.", ip);
            log.info("{}", userAgent);

            log.info("User {} is attempting to login", username);

            UserDetails user = serviceUserDetails.setCredentialsNonExpired(username, password);

            if(user == null){
                throw new Exception("Error is right here.");
            }

            boolean accountValid = serviceUserUtils.validateAccount(user);

            if (!accountValid) {
                throw new Exception(ExceptionMessages.ACCOUNT_NOT_VALID);
            }

            username = user.getUsername();
            var authorities = user.getAuthorities();

            log.info("{} {} is attempting to login.", authorities, username);

            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("location", location);

            String accessToken = ServiceTokenJW.generateToken(extraClaims, user);
            String refreshToken = ServiceTokenJW.refreshToken(user);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            SecurityContextHolder.getContext().setAuthentication(token);

            Authenticated authenticated = new Authenticated("171", "fakeuser@gmail.com", username, accessToken, refreshToken);
            Session session = new Session(authenticated, ip, userAgent);
            boolean sessionCreated = serviceSession.createSession(session);

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