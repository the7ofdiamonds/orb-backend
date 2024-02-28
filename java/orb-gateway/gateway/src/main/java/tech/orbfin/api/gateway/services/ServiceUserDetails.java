package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.model.user.User;
import tech.orbfin.api.gateway.model.user.UserEntity;

import tech.orbfin.api.gateway.repositories.IRepositoryUser;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class ServiceUserDetails
        implements UserDetailsService {
    private final IRepositoryUser iRepositoryUser;

    public ServiceUserDetails(IRepositoryUser iRepositoryUser){
        this.iRepositoryUser = iRepositoryUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = iRepositoryUser.findUserByUsername(username);

        User u = user.orElseThrow(() -> new UsernameNotFoundException("Error!"));

        return new UserEntity(u);
    }

    public UserDetails loadUserByEmail(String email) {
        Optional<User> user = iRepositoryUser.findUserByEmail(email);

        User u = user.orElseThrow();

        return new UserEntity(u);
    }
}