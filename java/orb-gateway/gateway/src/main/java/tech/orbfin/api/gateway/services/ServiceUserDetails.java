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

            if(user == null){
                throw new BadCredentialsException(ExceptionMessages.USERNAME_NOT_FOUND);
            }

            return new UserEntity(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean setCredentialsNonExpired(String username, String password, String confirmationCode){
        UserDetails user = loadUserByUsername(username);

        if(user.isCredentialsNonExpired()){
            return true;
        }

        return iRepositoryUserDetails.unexpireCredentials(username, password, confirmationCode);
    }

    public UserDetails loadUserByEmail(String email) {
        try {
            User user = serviceUserUtils.findUserByEmail(email);

            if (user == null) {
                throw new BadCredentialsException(ExceptionMessages.EMAIL_NOT_FOUND);
            }

            return new UserEntity(user);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public boolean setAccountNonExpired(String email, String confirmationCode){
        UserDetails user = loadUserByEmail(email);

        if(user.isAccountNonExpired()){
            return true;
        }

        return iRepositoryUserDetails.unexpireAccount(email, user.getUsername(), confirmationCode);
    }

    public boolean setAccountNonLocked(String email, String confirmationCode){
        UserDetails user = loadUserByEmail(email);

        if(user.isAccountNonLocked()){
            return true;
        }

        return iRepositoryUserDetails.unlockAccount(email, user.getUsername(), confirmationCode);
    }

    public boolean setEmailVerified(String email, String confirmationCode){
        UserDetails user = loadUserByEmail(email);

        if(user.isEnabled()){
            return true;
        }

        return iRepositoryUserDetails.enableAccount(email, user.getUsername(), confirmationCode);
    }
}