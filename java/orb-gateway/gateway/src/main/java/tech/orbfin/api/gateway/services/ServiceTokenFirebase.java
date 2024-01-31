package tech.orbfin.api.gateway.services;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.entities.token.Token;

@Slf4j
@AllArgsConstructor
@Service
public class ServiceTokenFirebase {
    @Autowired
    private ServiceAuthFirebase auth;

    public Token<String> buildToken(
            Map<String, Object> extraClaims,
            String uid
    ) throws FirebaseAuthException {
        try{
            return new Token<>(auth.getFirebaseAuth()
                    .createCustomToken(uid, extraClaims));
        } catch (FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }

    }

    public FirebaseToken verifyToken(Object idToken) throws FirebaseAuthException {
        try {
            return auth.getFirebaseAuth().verifyIdToken((String) idToken, true);
        }catch(FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }
    }

    public FirebaseToken refreshToken(String refreshToken) throws FirebaseAuthException {
        try {
            return auth.getFirebaseAuth().verifySessionCookie(refreshToken, true);
        } catch (FirebaseAuthException e){
            throw new FirebaseAuthException(e);
        }
    }
}
