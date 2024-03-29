package tech.orbfin.api.gateway.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import tech.orbfin.api.gateway.exceptions.AuthException;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.model.request.RequestChangePassword;
import tech.orbfin.api.gateway.model.request.RequestForgot;
import tech.orbfin.api.gateway.model.request.RequestUpdatePassword;
import tech.orbfin.api.gateway.model.response.ResponseChange;
import tech.orbfin.api.gateway.model.response.ResponseForgot;
import tech.orbfin.api.gateway.model.response.ResponseUpdate;
import tech.orbfin.api.gateway.services.*;
import tech.orbfin.api.gateway.services.firebase.ServiceTokenFirebase;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUserPassword {
    private final ServiceUserPassword serviceUserPassword;
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceUserDetails serviceUserDetails;
    private final ServiceTokenJW serviceTokenJW;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgot request) throws Exception {
        try {
            String email = request.getEmail();
            String username = request.getUsername();

            return ResponseEntity.ok().body(serviceUserPassword.forgotPassword(email, username));
        } catch (FirebaseAuthException | AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseForgot.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
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
    public ResponseEntity<ResponseChange> changePassword(@RequestBody RequestChangePassword request, @RequestHeader String Authorization) {
        try {
            String token = ServiceToken.getTokenFromAuthorization(Authorization);

            var header = ServiceToken.getTokenHeader(token);
            var algo = ServiceToken.getTokenAlgo(header);

            if (algo == null) {
                throw new AuthException("Please login to gain access and permission.");
            }

            String email = null;
            UserDetails user = null;

            if (algo.equals("RS256")) {
                email = serviceTokenFirebase.getEmailFromAccessToken(token);
                user = serviceUserDetails.loadUserByEmail(email);
            }

            if (algo.equals("HS256")) {
                user = serviceTokenJW.getValidUserFromAccessToken(token);
            }

            if (email == null) {
                throw new AuthException("Please login to gain access and permission.");
            }

            String password = user.getPassword();
            String newPassword = request.getPassword();
            String confirmPassword = request.getConfirmPassword();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserPassword.changePassword(email, password, newPassword, confirmPassword));
        } catch (FirebaseAuthException | AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
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
            String email = request.getEmail();
            String confirmationCode = request.getConfirmationCode();
            String password = request.getPassword();
            String confirmPassword = request.getConfirmPassword();
log.info(password);
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserPassword.updatePassword(email, confirmationCode, password, confirmPassword));
        } catch (FirebaseAuthException | AuthException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseUpdate.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
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
