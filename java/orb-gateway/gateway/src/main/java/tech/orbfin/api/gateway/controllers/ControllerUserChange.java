package tech.orbfin.api.gateway.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tech.orbfin.api.gateway.exceptions.AuthException;
import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.services.token.ServiceToken;
import tech.orbfin.api.gateway.services.token.ServiceTokenJW;
import tech.orbfin.api.gateway.services.user.ServiceUserChange;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.orbfin.api.gateway.services.user.ServiceUserDetails;
import tech.orbfin.api.gateway.services.firebase.ServiceTokenFirebase;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUserChange {
    private final ServiceUserChange serviceUserChange;
    private final ServiceTokenFirebase serviceTokenFirebase;
    private final ServiceUserDetails serviceUserDetails;
    private final ServiceTokenJW serviceTokenJW;

    @PostMapping("/change-username")
    public ResponseEntity<ResponseChange> changeUsername(@RequestBody RequestChangeUsername request, @RequestHeader String Authorization) throws Exception {
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
            String username = request.getUsername();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changeUsername(email, password, username));
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
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping("/change-name")
    public ResponseEntity<ResponseChange> changeName(@RequestBody RequestChangeName request, @RequestHeader String Authorization) {
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
            String newFirstName = request.getFirstName();
            String newLastName = request.getLastName();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changeName(email, password, newFirstName, newLastName));
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

    @PostMapping("/change-phone")
    public ResponseEntity<ResponseChange> changePhone(@RequestBody RequestChangePhone request, @RequestHeader String Authorization) {
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
            String newPhone = "+" + request.getPhone();

            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserChange.changePhone(email, password, newPhone));
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
}
