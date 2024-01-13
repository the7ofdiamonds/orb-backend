//package tech.orbfin.api.gateway.controllers;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import tech.orbfin.api.gateway.auth.AuthRequest;
//import tech.orbfin.api.gateway.auth.AuthResponse;
//import org.springframework.security.core.Authentication;
//import tech.orbfin.api.gateway.auth.RegisterRequest;
//import tech.orbfin.api.gateway.exceptions.LogoutException;
//import tech.orbfin.api.gateway.service.AuthService;
//import tech.orbfin.api.gateway.service.LogoutService;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping
//@RequiredArgsConstructor
//public class AuthRestController {
//
//    private final AuthService service;
//    private final LogoutService logoutService;
//
//    @PostMapping("/")
//    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
//        return ResponseEntity.ok(service.authenticate(request));
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(@RequestBody HttpServletRequest request,
//                                         HttpServletResponse response,
//                                         Authentication authentication) {
//        try {
//            logoutService.logout(request, response, authentication);
//            return ResponseEntity.ok("You have been logged out successfully.");
//        } catch (LogoutException e) {
//            return ResponseEntity.ok("Logout failed: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
//        return ResponseEntity.ok(service.register(request));
//    }
//
//    @PostMapping("/refresh")
//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        service.refreshToken(request, response);
//    }
//}
