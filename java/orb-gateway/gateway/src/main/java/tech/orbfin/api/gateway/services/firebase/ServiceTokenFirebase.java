package tech.orbfin.api.gateway.services.firebase;

import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SessionCookieOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tech.orbfin.api.gateway.services.IServiceToken;
import tech.orbfin.api.gateway.services.ServiceUserDetails;
import tech.orbfin.api.gateway.services.ServiceUserUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceTokenFirebase implements IServiceToken {
    private final ServiceAuthFirebase auth;
    private final ServiceUserDetails serviceUserDetails;

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

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        try {
            FirebaseToken tokenVerified = auth.firebaseAuth.verifyIdToken(accessToken, true);

            if (tokenVerified instanceof FirebaseToken) {
                return true;
            }

            return false;
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUsernameFromAccessToken(String token) {
        try {
            FirebaseToken accessTokenVerified = auth.firebaseAuth.verifyIdTokenAsync(token).get();

            if (accessTokenVerified == null) {
                return null;
            }

            String username = accessTokenVerified.getEmail();

            return username;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserDetails getValidUserFromAccessToken(String accessToken) {
        try {
            boolean tokenValid = isAccessTokenValid(accessToken);

            if (!tokenValid) {
                return null;
            }

            String email = getUsernameFromAccessToken(accessToken);

            if (email == null) {
                return null;
            }

            UserDetails user = serviceUserDetails.loadUserByEmail(email);

            if (user == null) {
                log.info("Username could not be found.");
                return null;
            }

            boolean enabled = user.isEnabled();

            if (!enabled) {
                log.info("Your account is not yet verified.");
                return null;
            }

            boolean accountLocked = user.isAccountNonLocked();

            if (!accountLocked) {
                log.info("Your account is locked.");
                return null;
            }

            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmailFromAccessToken(String token) throws FirebaseAuthException {
        try {
            FirebaseToken accessTokenVerified = auth.firebaseAuth.verifyIdToken(token);

            if (accessTokenVerified == null) {
                return null;
            }

            String email = accessTokenVerified.getEmail();

            return email;
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(e);
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
