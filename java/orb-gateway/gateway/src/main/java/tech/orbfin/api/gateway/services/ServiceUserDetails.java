package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.model.user.UserEntity;

import tech.orbfin.api.gateway.repositories.IRepositoryUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RequiredArgsConstructor
@Service
public class ServiceUserDetails implements UserDetailsService {
    private final ServiceUserUtils serviceUserUtils;
    private final IRepositoryUserDetails iRepositoryUserDetails;

    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            User user = serviceUserUtils.findUserByUsername(username);

            if (user == null) {
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_FOUND);
            }

            return new UserEntity(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails setCredentialsNonExpired(String username, String password) throws Exception {
        try {
            User validAccount = serviceUserUtils.validateCredentials(username, password);

            if (validAccount == null) {
                throw new BadCredentialsException(ExceptionMessages.ACCOUNT_VERIFY_ERROR);
            }

            boolean credentialUnexpired = iRepositoryUserDetails.unexpireCredentials(username, password);

            if (!credentialUnexpired) {
                throw new Exception(ExceptionMessages.CREDENTIALS_UNEXPIRED_ERROR);
            }

            UserDetails user = loadUserByUsername(username);

            return user;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails setCredentialsExpired(String username) throws Exception {
        try {
            UserDetails user = loadUserByUsername(username);

            boolean credentialUnexpired = iRepositoryUserDetails.expireCredentials(user.getUsername(), user.getPassword());

            if (!credentialUnexpired) {
                throw new Exception(ExceptionMessages.CREDENTIALS_UNEXPIRED_ERROR);
            }

            return user;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails loadUserByEmail(String email) {
        try {
            User user = serviceUserUtils.findUserByEmail(email);

            if (user == null) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_FOUND);
            }

            return new UserEntity(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    Subscription
    public boolean setAccountNonExpired(String email, String confirmationCode) {
        UserDetails user = loadUserByEmail(email);

        if (user.isAccountNonExpired()) {
            return true;
        }

        return iRepositoryUserDetails.unexpireAccount(email, user.getUsername(), confirmationCode);
    }

    public boolean setAccountExpired(String email, String confirmationCode) {
        UserDetails user = loadUserByEmail(email);

        if (user.isAccountNonExpired()) {
            return true;
        }

        return iRepositoryUserDetails.expireAccount(email, user.getUsername());
    }

//    Suspicious Activity
    public boolean setAccountNonLocked(String email, String confirmationCode) {
        UserDetails user = loadUserByEmail(email);

        if (user.isAccountNonLocked()) {
            return true;
        }

        return iRepositoryUserDetails.unlockAccount(email, user.getUsername(), confirmationCode);
    }

    public boolean setAccountLocked(String email, String confirmationCode) {
        UserDetails user = loadUserByEmail(email);

        if (user.isAccountNonLocked()) {
            return true;
        }

        return iRepositoryUserDetails.lockAccount(email, user.getUsername());
    }

    public boolean setEmailVerified(String email, String confirmationCode) {
        UserDetails user = loadUserByEmail(email);

        if (user.isEnabled()) {
            return true;
        }

        return iRepositoryUserDetails.enableAccount(email, user.getUsername(), confirmationCode);
    }

//    Account inactivity
    public boolean disableAccount(String email, String confirmationCode) {
        UserDetails user = loadUserByEmail(email);

        if (user.isEnabled()) {
            return true;
        }

        return iRepositoryUserDetails.disableAccount(email, user.getUsername());
    }
}