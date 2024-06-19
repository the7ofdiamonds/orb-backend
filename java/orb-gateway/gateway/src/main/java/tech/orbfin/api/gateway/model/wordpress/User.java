package tech.orbfin.api.gateway.model.wordpress;

import tech.orbfin.api.gateway.utils.PHP;

import java.util.Collection;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@Data
@Entity
public class User {

    @Id
    private Long id;

    private String email;

    private String username;

    private String password;

    private String first_name;

    private String last_name;

    private String phone;
    @Column(name = "provider_given_id")
    private String providerGivenID;
    @Column(name = "confirmation_code")
    private String confirmationCode;
    private String roles;
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

    public String getRoles() {
        try {
            PHP php = new PHP();
            return php.unserialize(roles);
        } catch (Exception e) {
            System.err.println("Error deserializing roles: " + e.getMessage());
            return null;
        }
    }

}
