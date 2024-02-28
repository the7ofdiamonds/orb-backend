package tech.orbfin.api.gateway.controllers;

import tech.orbfin.api.gateway.model.request.*;
import tech.orbfin.api.gateway.model.response.*;

import tech.orbfin.api.gateway.services.ServiceAuth;

import reactor.core.publisher.Mono;

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
public class ControllerAuth {
    private ServiceAuth serviceAuth;

    @PostMapping("/signup")
    public ResponseEntity<ResponseRegister> signup(@RequestBody RequestRegister request) {
        return ResponseEntity.ok().body(serviceAuth.register(request));
    }

    @PostMapping("/")
    public ResponseEntity<ResponseLogin> login(@RequestBody RequestLogin request) {
        return ResponseEntity.ok().body(serviceAuth.login(request));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<ResponseLogout>> logout(@RequestHeader RequestLogout request) {
        return Mono.just(ResponseEntity.ok().body(serviceAuth.logout(request)));
    }
}
