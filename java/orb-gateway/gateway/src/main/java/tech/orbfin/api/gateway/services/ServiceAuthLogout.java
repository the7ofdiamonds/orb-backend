package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.request.RequestLogout;
import tech.orbfin.api.gateway.model.response.ResponseLogout;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceAuthLogout {
    private final ServiceSession serviceSession;

    @Transactional
    public ResponseLogout logout(RequestLogout request) throws Exception {
        try {
            var accessToken = request.getAccessToken();
            var refreshToken = request.getRefreshToken();

            log.info("Service Auth Logout");

            String username = serviceSession.removeSession(accessToken);

            if (username == null) {
                throw new Exception(ExceptionMessages.SESSION_REMOVE_ERROR);
            }

            return new ResponseLogout(username);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.LOGOUT_ATTEMPT_ERROR + e.getMessage());
        }
    }

    public ResponseLogout logoutAll(String username) throws Exception {
        try {
            log.info("Service Auth Logout");

            boolean sessionsRemoved = serviceSession.removeAllSessions(username);

            if (!sessionsRemoved) {
                throw new Exception(ExceptionMessages.SESSION_REMOVE_ERROR);
            }

            return new ResponseLogout(username);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new Exception(ExceptionMessages.LOGOUT_ATTEMPT_ERROR + e.getMessage());
        }
    }
}
