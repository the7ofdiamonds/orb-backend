package tech.orbfin.api.gateway.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceTokenFirebase {
    private final ServiceAuthFirebase auth;

    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        try {
            return auth.firebaseAuth.verifyIdToken(idToken, true);
        }catch(FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }
    }

    public FirebaseToken verifyRefreshToken(String refreshToken) throws FirebaseAuthException {
        try {
            return auth.firebaseAuth.verifySessionCookie(refreshToken, true);
        } catch (FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }
    }
}
