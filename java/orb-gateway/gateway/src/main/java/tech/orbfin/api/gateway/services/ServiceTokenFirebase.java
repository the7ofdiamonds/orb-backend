package tech.orbfin.api.gateway.services;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import tech.orbfin.api.gateway.entities.user.UserEntity;
import tech.orbfin.api.gateway.repositories.RepositoryUser;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ServiceTokenFirebase {
    private RepositoryUser repositoryUser;


    public boolean verifyIdToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance(FirebaseApp.getInstance("orbfin")).verifyIdToken(idToken, true);
        Map<String,Object> claims = decodedToken.getClaims();
        Object ObjExpiration = claims.get("exp");
        Long expirationTimestamp = (Long) ObjExpiration;
        Date expiration = new Date(expirationTimestamp * 1000);

        if (expiration.before(new Date())) {
            return false;
        }

        if(!repositoryUser.existsByEmail(decodedToken.getEmail())){
            return false;
        }

        log.info(String.valueOf(expiration));
        return true;
    }

    public UserEntity getUser(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance(FirebaseApp.getInstance("orbfin")).verifyIdToken(idToken, true);

        String email = decodedToken.getEmail();
        Optional<UserEntity> userEntity = Optional.ofNullable(repositoryUser.findByEmail(email)
                .orElseThrow(() -> new Exception("User with the email " + email + " could not be found. Check your inbox.")));

        return userEntity.get();
    }

    public String getUsername(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance(FirebaseApp.getInstance("orbfin")).verifyIdToken(idToken, true);

        String email = decodedToken.getEmail();
        Optional<UserEntity> userEntity = Optional.ofNullable(repositoryUser.findByEmail(email)
                .orElseThrow(() -> new Exception("User with the email " + email + " could not be found. Check your inbox.")));

        if(userEntity.isPresent()){
            var username = userEntity.get().getUsername();
            log.info("Valid token found for user: {}", username);
            return username;
        }

        return null;
    }
}
