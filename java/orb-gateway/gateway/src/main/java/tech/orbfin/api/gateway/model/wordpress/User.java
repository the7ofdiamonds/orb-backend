package tech.orbfin.api.gateway.model.wordpress;

import java.util.Collection;

import jakarta.persistence.*;
import lombok.*;
import tech.orbfin.api.gateway.model.orb.Role;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class User {

    @Id
    private Long id;

    private String email;

    private String username;

    private String password;

    private String firstname;

    private String lastname;

    private String phone;
    @Column(name = "provider_given_id")
    private String providerGivenID;
    @Column(name = "confirmation_code")
    private String confirmationCode;
@Transient
    private Collection<Object> roles;
    @Column(name = "is_authenticated")
    private Boolean isAuthenticated;
    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired;
    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked;
    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired;
    @Column(name = "is_enabled")
    private Boolean isEnabled;
}
