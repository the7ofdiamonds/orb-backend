package tech.orbfin.api.gateway.controllers;

import lombok.extern.slf4j.Slf4j;
import tech.orbfin.api.gateway.request.RequestRegister;
import tech.orbfin.api.gateway.exceptions.LogoutException;
import tech.orbfin.api.gateway.service.ServiceAuth;
import tech.orbfin.api.gateway.request.RequestForgotPassword;
import tech.orbfin.api.gateway.request.RequestChangePassword;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ControllerAuthRest {

    private final ServiceAuth authService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody RequestRegister request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            return ResponseEntity.ok("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<Object> login(HttpServletRequest request) {
        try {
          return ResponseEntity.ok(authService.authenticate(request));
        } catch (Exception e){
            return  ResponseEntity.ok("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            authService.logout(request);
            return ResponseEntity.ok("You have been logged out successfully.");
        } catch (LogoutException e) {
            return ResponseEntity.ok("Logout failed: " + e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestBody RequestForgotPassword request) {
        try {

            authService.forgotPassword(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok("Unable to change password: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody RequestChangePassword request) {
        try {
            authService.changePassword(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.ok("Unable to change password: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(authService.refreshToken(request));
        } catch (Exception e) {
            return ResponseEntity.ok("Unable to provide refresh token: " + e.getMessage());
        }
    }

}
