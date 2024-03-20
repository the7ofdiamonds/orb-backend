package tech.orbfin.api.gateway.controllers;

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

    @PostMapping("/signup")
    public ResponseEntity<ResponseRegister> signup(@RequestBody RequestRegister request) {
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
            String firstname = request.getFirstname();
            String lastname = request.getLastname();
            String phone = request.getPhone();
            Object location = request.getLocation();
            log.info(email);
            log.info(username);
            log.info(password);
            log.info(confirmPassword);
            log.info(firstname);
            log.info(lastname);
            log.info(phone);
            log.info(location.toString());


            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserAccount.registerAccount(
                    email,
                    username,
                    password,
                    confirmPassword,
                    firstname,
                    lastname,
                    phone,
                    location));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseRegister.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseRegister.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/unlock-account")
    public ResponseEntity<ResponseUnlocked> unlockAccount(@RequestBody RequestVerify request) throws Exception {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            return ResponseEntity.ok().body(serviceUserAccount.unlockAccount(username, password, confirmationCode));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUnlocked.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUnlocked.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/remove-account")
    public ResponseEntity<ResponseRemoveAccount> removeAccount(@RequestBody RequestRemoveAccount request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String confirmationCode = request.getConfirmationCode();

            return ResponseEntity.ok().body(serviceUserAccount.removeAccount(username, password, confirmationCode));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseRemoveAccount.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseRemoveAccount.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}
