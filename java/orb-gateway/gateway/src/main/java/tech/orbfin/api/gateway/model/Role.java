import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.orbfin.api.gateway.repositories.IRepositoryUserRoles;
import tech.orbfin.api.gateway.utils.PHP;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Data
@Component
public class Role {
    // @Id - If Role is an entity, otherwise remove this annotation
    private String name;
    private Object capabilities;

    private final IRepositoryUserRoles iRepositoryUserRoles;
    private final PHP php;

    @Autowired
    public Role(IRepositoryUserRoles iRepositoryUserRoles, PHP php) {
        this.iRepositoryUserRoles = iRepositoryUserRoles;
        this.php = php;
    }

//    public Map<String, Object> getAllCapabilities() throws JsonProcessingException {
//        try {
//            var userRoles = iRepositoryUserRoles.getWPUserRoles();
//            var userRolesUnserialized = php.unserialize(userRoles);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class);
//
//            Map<String, Object> allCapabilities = new HashMap<>();
//
//            for (Map.Entry<String, String> entry : userRolesUnserialized.entrySet()) {
//                String roleName = entry.getKey();
//                String capabilitiesString = entry.getValue();
//
//                Map<String, Object> capabilities = objectMapper.readValue(capabilitiesString, mapType);
//                allCapabilities.put(roleName, capabilities);
//            }
//            return allCapabilities;
//        } catch (JsonProcessingException e) {
//            log.error("Error deserializing user roles: {}", e.getMessage());
//            throw new RuntimeException("Error deserializing user roles", e);
//        }
//    }

//    public Map<String, Object> getCapabilitiesByName(String roleName) throws JsonProcessingException {
//        try {
//            if (capabilities != null) {
//                Map<String, Object> allCapabilities = getAllCapabilities();
//                return (Map<String, Object>) allCapabilities.get(roleName);
//            }
//            return null;
//        } catch (JsonProcessingException e) {
//            log.error("Error retrieving capabilities for role {}: {}", roleName, e.getMessage());
//            throw new RuntimeException("Error retrieving capabilities for role " + roleName, e);
//        }
//    }
}
