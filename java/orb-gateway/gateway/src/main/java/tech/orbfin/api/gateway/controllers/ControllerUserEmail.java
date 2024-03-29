package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.services.ServiceUserEmail;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

import tech.orbfin.api.gateway.model.request.RequestAddEmail;
import tech.orbfin.api.gateway.model.request.RequestRemoveEmail;
import tech.orbfin.api.gateway.model.request.RequestVerify;
import tech.orbfin.api.gateway.model.response.ResponseAdd;
import tech.orbfin.api.gateway.model.response.ResponseRemove;
import tech.orbfin.api.gateway.model.response.ResponseVerify;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUserEmail {
    private ServiceUserEmail serviceUserEmail;

    @PostMapping("/verify-email")
    public ResponseEntity<ResponseVerify> verifyEmail(@RequestBody RequestVerify request) {
        try {
            String email = request.getEmail();
            String confirmationCode = request.getConfirmationCode();

            return ResponseEntity.ok().body(serviceUserEmail.verifyEmail(email, confirmationCode));
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

    @PostMapping("/add-email")
    public ResponseEntity<ResponseAdd> addEmail(@RequestBody RequestAddEmail request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newEmail = request.getNewEmail();
            String token = request.getToken();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserEmail.addEmail(username, password, newEmail, token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseAdd.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseAdd.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/remove-email")
    public ResponseEntity<ResponseRemove> removeEmail(@RequestBody RequestRemoveEmail request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String removeEmail = request.getRemoveEmail();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserEmail.removeEmail(username, password, removeEmail));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseRemove.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseRemove.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}
