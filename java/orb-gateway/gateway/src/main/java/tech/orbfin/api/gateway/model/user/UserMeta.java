package tech.orbfin.api.gateway.model.user;

import lombok.*;

import jakarta.persistence.*;


@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wp_usermeta")
public class UserMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "umeta_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "meta_key")
    private String metaKey;

    @Column(name = "meta_value", columnDefinition = "longtext")
    private String metaValue;

    public String getUserMetaValue(String requestedMetaKey) {
        if (requestedMetaKey != null && requestedMetaKey.equals(this.metaKey)) {
            return this.metaValue;
        }
        return null;
    }

    public void setUserMetaValue(String requestedMetaKey, String newMetaValue) {
        if (requestedMetaKey != null && requestedMetaKey.equals(this.metaKey)) {
            this.metaValue = newMetaValue;
        }
    }

    @Override
    public String toString() {
        return "UserMeta{id=" + id + ", userEntity=" + user + ", metaKey='" + metaKey + "', metaValue='" + metaValue + "'}";
    }
}
