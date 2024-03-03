package tech.orbfin.api.gateway.model.user;

import java.util.Collection;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.*;

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
    private String email;
    private String username;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserMeta> userMeta;
    private String firstname;
    private String lastname;
    private String phone;
    //    Is verified
//    Is email verified
    private boolean isAuthenticated;
    private Collection<Role> roles;
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