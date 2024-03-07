package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;

import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.model.user.UserEntity;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@RequiredArgsConstructor
@Service
public class ServiceUserDetails implements UserDetailsService {
    private final ServiceUserUtils serviceUserUtils;
    private final IRepositoryUser iRepositoryUser;

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

        if(user.isEnabled() && user.isCredentialsNonExpired() && user.isAccountNonExpired()){
            return true;
        }

        return iRepositoryUser.setAccountUnlocked(email, user.getUsername(), confirmationCode);
    }

    public boolean setAccountNonLocked(String email, String confirmationCode){
        UserDetails user = loadUserByEmail(email);

        if(user.isEnabled() && user.isAccountNonLocked()){
            return true;
        }

        return iRepositoryUser.setAccountUnlocked(email, user.getUsername(), confirmationCode);
    }

    public boolean setCredentialsNonExpired(String username){
        UserDetails user = loadUserByUsername(username);

        if(user.isCredentialsNonExpired()){
            return true;
        }

        return iRepositoryUser.setCredentialsNonExpired(user.getUsername(), user.getPassword());
    }

    public boolean setEmailVerified(String email, String confirmationCode){
        UserDetails user = loadUserByEmail(email);

        if(user.isEnabled()){
            return true;
        }

        return iRepositoryUser.setEmailVerified(email, user.getUsername(), confirmationCode);
    }
}