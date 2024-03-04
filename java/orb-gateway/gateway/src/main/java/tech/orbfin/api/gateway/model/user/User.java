package tech.orbfin.api.gateway.model.user;

import java.util.Collection;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    private String phone;

    @Column(name = "provider_given_id")
    private String providerGivenID;
@Transient
//    @ElementCollection
//    @CollectionTable(name = "wp_usermeta", joinColumns = @JoinColumn(name = "user_id"))
//    @MapKeyColumn(name = "meta_key") // Assuming meta_key is the role
//    @Column(name = "meta_value")
    private Collection<String> roles;

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
