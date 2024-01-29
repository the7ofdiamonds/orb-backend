package tech.orbfin.api.gateway.services;

import io.jsonwebtoken.*;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import tech.orbfin.api.gateway.entities.token.Token;
import tech.orbfin.api.gateway.repositories.RepositorySession;
import tech.orbfin.api.gateway.repositories.RepositoryUser;
import tech.orbfin.api.gateway.entities.user.UserEntity;

import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Slf4j
@Service
@Setter
@Getter
@RequiredArgsConstructor
public class ServiceTokenJW {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long expiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    @Autowired
    private RepositoryUser repositoryUser;
    @Autowired
    private RepositorySession repositorySession;

    private String buildToken(
            Map<String, Object> extraClaims,
            UserEntity user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), HS256)
                .compact();
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserEntity user
    ) {
        return buildToken(extraClaims, user, expiration);
    }

    public String refreshToken(
            UserEntity user
    ) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

//    Make parameters for secret keys
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
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

        if(currentTime > tokenExpiration){
            return true;
        }
        return false;
    }
}