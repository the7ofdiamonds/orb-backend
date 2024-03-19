package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.services.ServiceUser;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUser {
    private ServiceUser serviceUser;

    @PostMapping("/verify-email")
    public ResponseEntity<ResponseVerify> verifyEmail(@RequestBody RequestVerify request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            return ResponseEntity.ok().body(serviceUser.verifyEmail(username, password, confirmationCode));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseVerify.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseVerify.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgot request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            return ResponseEntity.ok().body(serviceUser.forgotPassword(email, username));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseForgot.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseForgot.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}
