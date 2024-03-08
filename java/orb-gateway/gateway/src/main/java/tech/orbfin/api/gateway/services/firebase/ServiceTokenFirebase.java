package tech.orbfin.api.gateway.services.firebase;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SessionCookieOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceTokenFirebase {
    private final ServiceAuthFirebase auth;

    public String createSessionCookie(String idToken) throws FirebaseAuthException {
        try {
            long expiresIn = TimeUnit.DAYS.toMillis(5);
            SessionCookieOptions options = SessionCookieOptions.builder()
                    .setExpiresIn(expiresIn)
                    .build();

            return FirebaseAuth.getInstance().createSessionCookie(idToken, options);
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e);
        }
    }

    public ApiFuture<FirebaseToken> verifyToken(String idToken) throws Exception {
        try {
            ApiFuture<FirebaseToken> tokenVerified = auth.firebaseAuth.verifyIdTokenAsync(idToken, true);
//            FirebaseToken tokenVerified = auth.firebaseAuth.verifyIdToken(idToken, false);
            if (tokenVerified.get() == null) {
                throw new Exception("Token not valid");
            }

            return tokenVerified;
        } catch (Exception e) {
            throw new Exception("Token not valid");
        }
    }

    public boolean verifyRefreshToken(String refreshToken) throws FirebaseAuthException {
        try {
            FirebaseToken refreshTokenVerified = auth.firebaseAuth.verifySessionCookie(refreshToken, true);
            auth.firebaseAuth.verifyIdToken(refreshToken).getEmail();
            return refreshTokenVerified != null;
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e);
        }
    }

    public String getEmailFromAccessToken(String token) {
        try {
            FirebaseToken accessTokenVerified = auth.firebaseAuth.verifyIdTokenAsync(token).get();

            if (accessTokenVerified == null) {
                return null;
            }

            String email = accessTokenVerified.getEmail();

            return email;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmailFromRefreshToken(String refreshToken) throws FirebaseAuthException {
        try {
            FirebaseToken refreshTokenVerified = auth.firebaseAuth.verifySessionCookie(refreshToken, true);
            String email = refreshTokenVerified.getEmail();
            String username = refreshTokenVerified.getName();
            log.info(email);
            log.info(username);

            return email;
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e);
        }
    }
}
