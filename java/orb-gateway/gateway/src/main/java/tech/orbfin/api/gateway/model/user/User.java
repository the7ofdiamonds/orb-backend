package tech.orbfin.api.gateway.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Table(name = "wp_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_login")
    private String username;

    @Column(name = "user_pass")
    private String password;

    @Column(name = "user_nicename")
    private String nicename;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_url")
    private String url;

    @Column(name = "user_registered")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registered;

    @Column(name = "user_activation_key")
    private String activationKey;

    @Column(name = "user_status")
    private int status;

    @Column(name = "display_name")
    private String displayName;

    // Getters and setters

    // Constructors

    // Other methods as needed
}
