package tech.orbfin.api.gateway.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.orbfin.api.gateway.request.*;
import tech.orbfin.api.gateway.response.*;
import tech.orbfin.api.gateway.services.ServiceAuth;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@Component
@AllArgsConstructor
public class ControllerAuthRest {
    private final ServiceAuth authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseRegister> signup(@RequestBody RequestRegister request) {
        return authService.register(request);
    }

    @PostMapping("/")
    public ResponseEntity<ResponseLogin> login(@RequestBody RequestLogin request) {
        return authService.login(request);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseChange> changePassword(@RequestBody RequestChangePassword request) {
        return authService.changePassword(request);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<ResponseLogout>> logout(@RequestHeader RequestLogout request) {
        return authService.logout(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseForgot> forgotPassword(@RequestBody RequestForgotPassword request) {
        return authService.forgotPassword(request);
    }
}
