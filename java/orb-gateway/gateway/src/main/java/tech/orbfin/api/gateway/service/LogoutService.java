//package tech.orbfin.api.gateway.service;
//
//import tech.orbfin.api.gateway.exceptions.LogoutException;
//import tech.orbfin.api.gateway.repositories.TokenRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class LogoutService implements LogoutHandler {
//
//    private final TokenRepository tokenRepository;
//
//    @Override
//    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new LogoutException("Invalid Authorization header");
//        }
//
//        jwt = authHeader.substring(7);
//        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
//
//        if (storedToken != null) {
//            storedToken.setExpired(true);
//            storedToken.setRevoked(true);
//            tokenRepository.save(storedToken);
//            SecurityContextHolder.clearContext();
//        } else {
//            throw new LogoutException("Token not found or invalid");
//        }
//    }
//}