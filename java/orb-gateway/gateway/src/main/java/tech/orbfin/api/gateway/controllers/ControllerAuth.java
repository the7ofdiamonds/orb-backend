package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.services.ServiceAuthLogin;
import tech.orbfin.api.gateway.services.ServiceAuthLogout;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping
@Component
public class ControllerAuth {
    private final ServiceAuthLogin serviceAuthLogin;
    private final ServiceAuthLogout serviceAuthLogout;

    @PostMapping("/")
    public ResponseEntity<ResponseLogin> login(@RequestBody RequestLogin request) {
        return ResponseEntity.ok().body(serviceAuthLogin.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseLogout> logout(@RequestHeader RequestLogout request) {
        return ResponseEntity.ok().body(serviceAuthLogout.logout(request.getToken()));
    }
}
