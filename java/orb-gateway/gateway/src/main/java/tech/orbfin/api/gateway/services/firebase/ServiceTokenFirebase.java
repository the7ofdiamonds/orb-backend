package tech.orbfin.api.gateway.services.firebase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.services.firebase.ServiceAuthFirebase;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceTokenFirebase {
    private final ServiceAuthFirebase auth;

    public boolean verifyToken(String idToken) throws FirebaseAuthException {
        try {
            FirebaseToken tokenVerified = auth.firebaseAuth.verifyIdToken(idToken, true);

            return tokenVerified != null;
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e);
        }
    }

    public boolean verifyRefreshToken(String refreshToken) throws FirebaseAuthException {
        try {
            FirebaseToken refreshTokenVerified = auth.firebaseAuth.verifySessionCookie(refreshToken, true);

            return refreshTokenVerified != null;
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e);
        }
    }
}
