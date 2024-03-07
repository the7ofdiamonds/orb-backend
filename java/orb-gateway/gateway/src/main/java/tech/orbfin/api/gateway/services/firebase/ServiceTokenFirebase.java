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

    public FirebaseToken verifyToken(String idToken) throws Exception {
        try {
            FirebaseToken tokenVerified = auth.firebaseAuth.verifyIdToken(idToken, false);

            return tokenVerified;
        } catch (FirebaseAuthException e) {
            throw new Exception("Token not valid");
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
