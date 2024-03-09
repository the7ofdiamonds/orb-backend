package tech.orbfin.api.gateway.services;

import org.springframework.security.core.userdetails.UserDetails;
import tech.orbfin.api.gateway.model.user.User;

import java.util.*;
import java.security.Key;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.model.user.UserEntity;

@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
@Service
public class ServiceTokenJW {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value(value = "${application.security.jwt.algorithm}")
    public SignatureAlgorithm ALGORITHM;
    @Value("${application.security.jwt.access-token.expiration}")
    private long expiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), ALGORITHM)
                .compact();
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails user
    ) {
        return buildToken(extraClaims, user, expiration);
    }

    public String refreshToken(
            UserDetails user
    ) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    //    Make parameters for secret keys
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        long currentTime = (new Date()).getTime();
        long tokenExpiration = (extractExpiration(token)).getTime();

        return currentTime <= tokenExpiration;
    }
}