package tech.orbfin.api.gateway.controllers;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.orbfin.api.gateway.model.request.RequestDeleteAccount;
import tech.orbfin.api.gateway.model.response.ResponseDeleteAccount;

import tech.orbfin.api.gateway.services.ServiceAdmin;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerAdmin {
    private ServiceAdmin serviceAdmin;

    @PostMapping("/delete-account")
    public ResponseEntity<ResponseDeleteAccount> removeAccount(@RequestBody RequestDeleteAccount request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String confirmationCode = request.getConfirmationCode();

            return ResponseEntity.ok().body(serviceAdmin.deleteAccount(email, username, confirmationCode));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDeleteAccount.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDeleteAccount.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}
