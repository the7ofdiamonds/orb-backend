package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.user.Capabilities;
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

            return new UserEntity(user, new Capabilities(iRepositoryUserDetails));
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
                throw new Exception(ExceptionMessages.CREDENTIALS_EXPIRED_ERROR);
            }

            return user;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails loadUserByEmail(String email) throws Exception {
        try {
            User user = serviceUserUtils.findUserByEmail(email);

            if (user == null) {
                throw new Exception(ExceptionMessages.EMAIL_NOT_FOUND);
            }

            return new UserEntity(user, new Capabilities(iRepositoryUserDetails));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    //    Subscription
    public boolean setAccountNonExpired(String email, String confirmationCode) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (user.isAccountNonExpired()) {
                return true;
            }

            boolean accountUnexpired = iRepositoryUserDetails.unexpireAccount(email, user.getUsername(), confirmationCode);

            if (!accountUnexpired) {
                throw new Exception(ExceptionMessages.ACCOUNT_UNEXPIRE_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }


    //    Subscription
    public boolean setAccountExpired(String email) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (!user.isAccountNonExpired()) {
                return true;
            }

            boolean accountExpired = iRepositoryUserDetails.expireAccount(email, user.getUsername());

            if (!accountExpired) {
                throw new Exception(ExceptionMessages.ACCOUNT_EXPIRE_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public boolean setAccountNonLocked(String email, String confirmationCode) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (user.isAccountNonLocked()) {
                return true;
            }

            boolean accountUnlocked = iRepositoryUserDetails.unlockAccount(email, user.getUsername(), confirmationCode);

            if (!accountUnlocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_UNLOCKED_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    //    Suspicious Activity
    public boolean setAccountLocked(String email, String username) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (user.isAccountNonLocked()) {
                return true;
            }

            boolean accountLocked = iRepositoryUserDetails.lockAccount(email, username);

            if (!accountLocked) {
                throw new Exception(ExceptionMessages.ACCOUNT_LOCKED_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public boolean enableAccount(String email, String confirmationCode) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (user.isEnabled()) {
                return true;
            }

            boolean accountEnabled = iRepositoryUserDetails.enableAccount(email, user.getUsername(), confirmationCode);

            if (!accountEnabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    //    Account inactivity
    public boolean disableAccount(String email, String username) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (!user.isEnabled()) {
                return true;
            }

            boolean accountDisabled = iRepositoryUserDetails.disableAccount(email, username);

            if (!accountDisabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_DISABLE_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}