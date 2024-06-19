package tech.orbfin.api.gateway.services.token;

import org.springframework.security.core.userdetails.UserDetails;

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
import tech.orbfin.api.gateway.services.user.ServiceUserDetails;

@Slf4j
@RequiredArgsConstructor
@Setter
@Getter
@Service
public class ServiceTokenJW implements IServiceToken {
    private static String issuer;
    private static String secretKey;
    public static SignatureAlgorithm ALGORITHM;
    private static long expiration;
    private static long refreshExpiration;
    private final ServiceUserDetails serviceUserDetails;

    @Value("${application.security.jwt.issuer}")
    private void setIssuer(String issuer) {
        ServiceTokenJW.issuer = issuer;
    }

    @Value("${application.security.jwt.secret-key}")
    private void setSecretKey(String secretKey) {
        ServiceTokenJW.secretKey = secretKey;
    }

    @Value(value = "${application.security.jwt.algorithm}")
    private void setALGORITHM(String ALGORITHM) {
        ServiceTokenJW.ALGORITHM = SignatureAlgorithm.valueOf(ALGORITHM);
    }

    @Value("${application.security.jwt.access-token.expiration}")
    private void setExpiration(String expiration) {
        ServiceTokenJW.expiration = Long.parseLong(expiration);
    }

    @Value("${application.security.jwt.refresh-token.expiration}")
    private void setRefreshExpiration(String refreshExpiration) {
        ServiceTokenJW.refreshExpiration = Long.parseLong(refreshExpiration);
    }

    private static long getExpirationTime(long expiresIn) {
        return System.currentTimeMillis() + expiresIn;
    }

    private static String buildToken(
            Map<String, Object> extraClaims,
            UserDetails user,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setIssuer(issuer)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), ALGORITHM)
                .compact();
    }

    public static String generateToken(
            Map<String, Object> extraClaims,
            UserDetails user
    ) {
        long expires = getExpirationTime(expiration);

        return buildToken(extraClaims, user, expires);
    }

    public static String refreshToken(
            UserDetails user
    ) {
        long expires = getExpirationTime(refreshExpiration);

        return buildToken(new HashMap<>(), user, expires);
    }

    //    Make parameters for secret keys
    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private String getTokenIssuer(String accessToken) {
        return extractClaim(accessToken, Claims::getIssuer);
    }

    public static String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
        final Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private static long extractExpiration(String token) {
        return (extractClaim(token, Claims::getExpiration)).getTime();
    }

    public static boolean isTokenExpired(String token) {
        long currentTime = (new Date()).getTime();
        long tokenExpiration = extractExpiration(token);

        return currentTime < tokenExpiration;
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        String validIssuer = getTokenIssuer(accessToken);

        if (!validIssuer.equals(issuer)) {
            log.info("Issuer is not valid");
            return false;
        }

        boolean tokenExpired = isTokenExpired(accessToken);

        if (!tokenExpired) {
            log.info("Token is expired.");
            return false;
        }

        return true;
    }

    @Override
    public String getUsernameFromAccessToken(String accessToken) {
        boolean tokenExpired = isTokenExpired(accessToken);

        if (!tokenExpired) {
            log.info("Token is expired.");
            return null;
        }

        String username = extractUsername(accessToken);

        if (username == null) {
            log.info("Username could not be extracted from token.");
            return null;
        }

        return username;
    }

    @Override
    public UserDetails getValidUserFromAccessToken(String accessToken) {
        boolean validToken = isAccessTokenValid(accessToken);

        if (!validToken) {
            log.info("Token is not valid");
            return null;
        }

        String username = getUsernameFromAccessToken(accessToken);

        if (username == null) {
            log.info("Username could not be found in token.");
            return null;
        }

        return serviceUserDetails.validAccount(username);
    }
}