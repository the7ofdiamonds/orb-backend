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

    private String firstname;

    private String lastname;

    private String phone;

    private String providerGivenID;

    private String confirmationCode;

@Transient
//    @ElementCollection
//    @CollectionTable(name = "wp_usermeta", joinColumns = @JoinColumn(name = "user_id"))
//    @MapKeyColumn(name = "meta_key") // Assuming meta_key is the role
//    @Column(name = "meta_value")
    private Collection<String> roles;

    private Boolean isAuthenticated;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Boolean isEnabled;
}
