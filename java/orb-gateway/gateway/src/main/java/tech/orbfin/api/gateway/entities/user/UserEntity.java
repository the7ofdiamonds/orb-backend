package tech.orbfin.api.gateway.entities.user;

import tech.orbfin.api.gateway.entities.token.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users_orbfin")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue
    private Integer id;
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private String firstname;
    private String lastname;
    private boolean isAuthenticated;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();
    @OneToMany(mappedBy = "userid", fetch = FetchType.EAGER)
    private List<Token> tokens;

    public UserEntity(String username,  String password, String email, String firstname, String lastname){
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return mapRolesToAuthorities(this.roles);
    }

    public Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}