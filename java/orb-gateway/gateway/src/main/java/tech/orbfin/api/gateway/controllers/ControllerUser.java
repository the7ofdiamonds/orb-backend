package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.services.ServiceUser;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Component
public class ControllerUser {
    private ServiceUser serviceUser;

    @PostMapping("/signup")
    public ResponseEntity<ResponseRegister> signup(@RequestBody RequestRegister request) {
        return ResponseEntity.ok().body(serviceUser.register(request));
    }
// isEnabled
//    @PostMapping("/verify-email")
//    public ResponseEntity<ResponseVerify> verifyEmail(@RequestBody RequestVerify request) {
//        return ResponseEntity.ok().body(serviceUser.verifyEmail(request));
//    }
// isAccountNonLocked
//    @PostMapping("/unlock-account")
//    public ResponseEntity<ResponseRemove> unlockAccount(@RequestBody RequestUnlockAccount request) {
//        return ResponseEntity.ok().body(serviceUser.unlockAccount(request));
//    }
//
//    @PostMapping("/add-email")
//    public ResponseEntity<ResponseAdd> addEmail(@RequestBody RequestAddEmail request) {
//        return ResponseEntity.ok().body(serviceUser.addEmail(request));
//    }

    @PostMapping("/change-username")
    public ResponseEntity<ResponseChange> changeUsername(@RequestBody RequestChangeUsername request) {
        return ResponseEntity.ok().body(serviceUser.changeUsername(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseChange> changePassword(@RequestBody RequestChangePassword request) {
        return ResponseEntity.ok().body(serviceUser.changePassword(request));
    }
// isCredentialsNonExpired
//    @PostMapping("/update-password")
//    public ResponseEntity<ResponseUpdate> updatePassword(@RequestBody RequestUpdatePassword request) {
//        return ResponseEntity.ok().body(serviceUser.updatePassword(request));
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgot request) {
        return ResponseEntity.ok().body(serviceUser.forgotPassword(request));
    }

    @PostMapping("/change-name")
    public ResponseEntity<ResponseChange> changeName(@RequestBody RequestChangeName request) {
        return ResponseEntity.ok().body(serviceUser.changeName(request));
    }

    @PostMapping("/change-phone")
    public ResponseEntity<ResponseChange> changePhone(@RequestBody RequestChangePhone request) {
        return ResponseEntity.ok().body(serviceUser.changePhone(request));
    }

//    @PostMapping("/remove-email")
//    public ResponseEntity<ResponseRemove> removeEmail(@RequestBody RequestRemoveEmail request) {
//        return ResponseEntity.ok().body(serviceUser.removeEmail(request));
//    }
//
//    @PostMapping("/delete-account")
//    public ResponseEntity<ResponseDelete> deleteAccount(@RequestBody RequestDeleteAccount request) {
//        return ResponseEntity.ok().body(serviceUser.deleteAccount(request));
//    }
}
