package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.services.ServiceUser;
import tech.orbfin.api.gateway.services.ServiceUserAccount;
import tech.orbfin.api.gateway.services.ServiceUserChange;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.UserCreationException;

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
    private ServiceUserAccount serviceUserAccount;
    private ServiceUserChange serviceUserChange;

    @PostMapping("/signup")
    public ResponseEntity<ResponseRegister> signup(@RequestBody RequestRegister request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(serviceUserAccount.registerAccount(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseRegister.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    // isEnabled
    @PostMapping("/verify-email")
    public ResponseEntity<ResponseVerify> verifyEmail(@RequestBody RequestVerify request) {
        try {
            return ResponseEntity.ok().body(serviceUser.verifyEmail(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseVerify.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    // isAccountNonLocked
    @PostMapping("/unlock-account")
    public ResponseEntity<ResponseUnlocked> unlockAccount(@RequestBody RequestVerify request) throws Exception {
        try {
            return ResponseEntity.ok().body(serviceUserAccount.unlockAccount(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUnlocked.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    @PostMapping("/add-email")
    public ResponseEntity<ResponseAdd> addEmail(@RequestBody RequestAddEmail request) throws Exception {
        try {
            return ResponseEntity.ok().body(serviceUserChange.addEmail(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseAdd.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            return ResponseEntity.ok().body(serviceUserChange.changeUsername(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            return ResponseEntity.ok().body(serviceUserChange.changePassword(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    // isCredentialsNonExpired
    @PostMapping("/update-password")
    public ResponseEntity<ResponseUpdate> updatePassword(@RequestBody RequestUpdatePassword request) throws Exception {
        try {
            return ResponseEntity.ok().body(serviceUserChange.updatePassword(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseUpdate.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgot request) throws Exception {
        try {
            return ResponseEntity.ok().body(serviceUser.forgotPassword(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseForgot.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    @PostMapping("/change-name")
    public ResponseEntity<ResponseChange> changeName(@RequestBody RequestChangeName request) {
        try {
            return ResponseEntity.ok().body(serviceUserChange.changeName(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            return ResponseEntity.ok().body(serviceUserChange.changePhone(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseChange.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
            return ResponseEntity.ok().body(serviceUserChange.removeEmail(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseRemove.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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

    @PostMapping("/delete-account")
    public ResponseEntity<ResponseDeleteAccount> deleteAccount(@RequestBody RequestDeleteAccount request) throws Exception {
        try {
            return ResponseEntity.ok().body(serviceUserAccount.removeAccount(request));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDeleteAccount.builder()
                            .errorMessage(e.getMessage())
                            .build());
        } catch (UserCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
