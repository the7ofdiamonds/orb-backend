package tech.orbfin.api.gateway.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import tech.orbfin.api.gateway.exceptions.AuthException;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.services.ServiceUserAccount;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUserAccount {
    private ServiceUserAccount serviceUserAccount;

    @PostMapping("/unlock-account")
    public ResponseEntity<ResponseUnlocked> unlockAccount(@RequestBody RequestVerify request) throws Exception {
        try {
            String email = request.getEmail();
            String confirmationCode = request.getConfirmationCode();

            return ResponseEntity.ok().body(serviceUserAccount.unlockAccount(email, confirmationCode));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUnlocked.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUnlocked.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping("/remove-account")
    public ResponseEntity<ResponseRemoveAccount> removeAccount(@RequestBody RequestRemoveAccount request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();
log.info(confirmationCode);
            return ResponseEntity.ok().body(serviceUserAccount.removeAccount(email, password, confirmationCode));
        } catch (FirebaseAuthException | AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseRemoveAccount.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseRemoveAccount.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseRemoveAccount.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
