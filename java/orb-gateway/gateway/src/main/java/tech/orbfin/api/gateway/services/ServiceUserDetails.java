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

    public boolean setCredentialsNonExpired(String username, String password, String confirmationCode){
        UserDetails user = loadUserByUsername(username);

        if(user.isCredentialsNonExpired()){
            return true;
        }

        return iRepositoryUser.unexpireCredentials(username, password, confirmationCode);
    }
}