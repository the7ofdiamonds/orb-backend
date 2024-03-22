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
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.model.request.RequestChangePassword;
import tech.orbfin.api.gateway.model.request.RequestForgot;
import tech.orbfin.api.gateway.model.request.RequestUpdatePassword;
import tech.orbfin.api.gateway.model.response.ResponseChange;
import tech.orbfin.api.gateway.model.response.ResponseForgot;
import tech.orbfin.api.gateway.model.response.ResponseUpdate;
import tech.orbfin.api.gateway.services.ServiceUserPassword;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUserPassword {
    private ServiceUserPassword serviceUserPassword;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgot request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            return ResponseEntity.ok().body(serviceUserPassword.forgotPassword(email, username));
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

    @PostMapping("/change-password")
    public ResponseEntity<ResponseChange> changePassword(@RequestBody RequestChangePassword request) {
        try {
            String username = request.getUsername();
            String password = request.getPassword();
            String newPassword = request.getNewPassword();
            String confirmPassword = request.getConfirmationPassword();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserPassword.changePassword(username, password, newPassword, confirmPassword));
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
            String username = request.getUsername();
            String confirmationCode = request.getConfirmationCode();
            String newPassword = request.getNewPassword();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserPassword.updatePassword(username, confirmationCode, newPassword));
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
}
