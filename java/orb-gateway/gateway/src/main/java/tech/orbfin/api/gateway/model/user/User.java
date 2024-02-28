package tech.orbfin.api.gateway.model.user;

import java.util.Collection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wp_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_email")
    private String email;
    @Column(name = "display_name")
    private String username;
    @Column(name = "user_pass")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserMeta> userMeta;
    @Transient
    private String firstname;
    @Transient
    private String lastname;
    @Transient
    private String phone;
    //    Is verified
//    Is email verified
    @Transient
    private boolean isAuthenticated;
    @Transient
    private Collection<Role> roles;
    @Transient
    private String providerGivenID;

    private Map<String, String> getMetaValues() {
        if (userMeta != null) {
            return userMeta.stream()
                    .collect(Collectors.toMap(UserMeta::getMetaKey, UserMeta::getMetaValue));
        }
        return Collections.emptyMap();
    }

    public String getMetaValue(String metaKey) {
        return getMetaValues().get(metaKey);
    }

    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", phone='" + phone + '\'' +
                ", isAuthenticated=" + isAuthenticated +
                ", roles=" + roles +
                ", providerGivenID='" + providerGivenID + '\'' +
                '}';
    }
}