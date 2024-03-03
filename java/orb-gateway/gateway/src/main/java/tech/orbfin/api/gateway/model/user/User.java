package tech.orbfin.api.gateway.model.user;

import java.util.Collection;

import jakarta.persistence.*;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Setter
@Getter
@Table(name = "wp_users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String password;
    @Column(name = "first_name")
    private String firstname;
    @Column(name = "last_name")
    private String lastname;
    private String phone;
    private Collection<Role> roles;
    @Column(name = "provider_given_id")
    private String providerGivenID;
    @Column(name = "is_authenticated")
    public Boolean isAuthenticated = true;
    @Column(name = "is_account_non_expired")
    public Boolean isAccountNonExpired;
    @Column(name = "is_account_non_locked")
    public Boolean isAccountNonLocked;
    @Column(name = "is_credentials_non_expired")
    public Boolean isCredentialsNonExpired;
    @Column(name = "is_enabled")
    public Boolean isEnabled;
}