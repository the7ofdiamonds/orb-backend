package tech.orbfin.api.gateway.services;

import lombok.extern.slf4j.Slf4j;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.wordpress.User;
import tech.orbfin.api.gateway.model.UserEntity;

import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
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

            boolean credentialUnexpired = iRepositoryUserDetails.unexpireCredentials(validAccount.getEmail(), username);

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
            User user = serviceUserUtils.findUserByUsername(username);

            boolean credentialUnexpired = iRepositoryUserDetails.expireCredentials(user.getEmail(), username);

            if (!credentialUnexpired) {
                throw new Exception(ExceptionMessages.CREDENTIALS_EXPIRED_ERROR);
            }

            return new UserEntity(user);
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

            return new UserEntity(user);
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
    public boolean setAccountLocked(String email, String password) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);

            if (user.isAccountNonLocked()) {
                return true;
            }

            boolean accountLocked = iRepositoryUserDetails.lockAccount(email, password);

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

            boolean accountEnabled = iRepositoryUserDetails.enableAccount(email, user.getPassword(), confirmationCode);

            if (!accountEnabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_ENABLED_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    //    Account inactivity
    public boolean disableAccount(String email, String password) throws Exception {
        try {
            UserDetails user = loadUserByEmail(email);
log.info(String.valueOf(user.isEnabled()));
            if (!user.isEnabled()) {
                return true;
            }

            boolean accountDisabled = iRepositoryUserDetails.disableAccount(email, password);

            if (!accountDisabled) {
                throw new Exception(ExceptionMessages.ACCOUNT_DISABLE_ERROR);
            }

            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public UserDetails validAccount(String username) {
        UserDetails user = loadUserByUsername(username);

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
    }
}