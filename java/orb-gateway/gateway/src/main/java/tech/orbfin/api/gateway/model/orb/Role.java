package tech.orbfin.api.gateway.model.orb;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class Role {
    public static final String SUBSCRIBER = "subscriber";
    public static final String CONTRIBUTOR = "contributor";
    public static final String EDITOR = "editor";
    public static final String ADMIN = "administrator";

    private String name;
    private Map<String, Boolean> capabilities;
}