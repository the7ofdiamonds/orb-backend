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
import tech.orbfin.api.gateway.model.request.RequestRegister;
import tech.orbfin.api.gateway.model.response.ResponseRegister;
import tech.orbfin.api.gateway.services.user.ServiceUserAccount;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUserSignup {
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
            String nicename = request.getNicename();
            Object location = request.getLocation();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserAccount.registerAccount(
                    email,
                    username,
                    password,
                    confirmPassword,
                    firstname,
                    lastname,
                    phone,
                    nicename,
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
}
