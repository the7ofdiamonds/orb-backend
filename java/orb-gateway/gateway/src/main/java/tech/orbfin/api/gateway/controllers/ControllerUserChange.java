package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.services.ServiceUserChange;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

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
public class ControllerUserChange {
    private ServiceUserChange serviceUserChange;

    @PostMapping("/add-email")
    public ResponseEntity<ResponseAdd> addEmail(@RequestBody RequestAddEmail request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.addEmail(request));
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

    @PostMapping("/change-username")
    public ResponseEntity<ResponseChange> changeUsername(@RequestBody RequestChangeUsername request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changeUsername(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseChange> changePassword(@RequestBody RequestChangePassword request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changePassword(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<ResponseUpdate> updatePassword(@RequestBody RequestUpdatePassword request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.updatePassword(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUpdate.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseUpdate.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/change-name")
    public ResponseEntity<ResponseChange> changeName(@RequestBody RequestChangeName request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changeName(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/change-phone")
    public ResponseEntity<ResponseChange> changePhone(@RequestBody RequestChangePhone request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changePhone(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/remove-email")
    public ResponseEntity<ResponseRemove> removeEmail(@RequestBody RequestRemoveEmail request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.removeEmail(request));
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
