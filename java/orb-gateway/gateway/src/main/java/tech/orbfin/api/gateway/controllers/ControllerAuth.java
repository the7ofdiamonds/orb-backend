package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.services.authentication.ServiceAuthLogin;
import tech.orbfin.api.gateway.services.authentication.ServiceAuthLogout;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import lombok.extern.slf4j.Slf4j;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
@Component
public class ControllerAuth {
    private final ServiceAuthLogin serviceAuthLogin;
    private final ServiceAuthLogout serviceAuthLogout;

    @PostMapping("/")
    public ResponseEntity<ResponseLogin> login(@RequestHeader(value = "User-Agent") String userAgent, @RequestBody RequestLogin login) {
        try {
            String username = login.getUsername();
            String password = login.getPassword();
            Object location = login.getLocation();
//            String ip = String.valueOf(request.getRemoteUser());
String ip = "123.123.1234";
log.info(username);
            return ResponseEntity.ok().body(serviceAuthLogin.login(ip, userAgent, username, password, location));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseLogin.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseLogin.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseLogout> logout(@RequestHeader RequestLogout request) {
        try {
            String accessToken = request.getAccessToken();
            String refreshToken = request.getRefreshToken();

            return ResponseEntity.ok().body(serviceAuthLogout.logout(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseLogout.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseLogout.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ResponseLogout> logoutAll(@RequestBody RequestLogoutAll request) {
        try {
            return ResponseEntity.ok().body(serviceAuthLogout.logoutAll(request.getUsername()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseLogout.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseLogout.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}
