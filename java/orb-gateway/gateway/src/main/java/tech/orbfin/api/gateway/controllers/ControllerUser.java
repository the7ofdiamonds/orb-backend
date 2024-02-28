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

    @PostMapping("/change-password")
    public ResponseEntity<ResponseChange> changePassword(@RequestBody RequestChange request) {
        return ResponseEntity.ok().body(serviceUser.changePassword(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgot request) {
        return ResponseEntity.ok().body(serviceUser.forgotPassword(request));
    }
}
