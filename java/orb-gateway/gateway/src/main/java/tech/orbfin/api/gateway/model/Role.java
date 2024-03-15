package tech.orbfin.api.gateway.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserRoles;
import tech.orbfin.api.gateway.utils.PHP;

import java.util.Map;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Component
public class Role {
    @Id
    private String name;
    private Object capabilities;

    private final IRepositoryUserRoles iRepositoryUserRoles;
    private final PHP php;

    public Object getCapabilities() throws JsonProcessingException {
        var userRoles = iRepositoryUserRoles.getWPUserRoles();
        var userRolesUnserielized = php.unserialize(userRoles);

        String[] roleStrings = userRolesUnserielized.toString()
                .substring(1, userRolesUnserielized.toString().length() - 1)
                .split(",\\s*(?![^\\{\\}]*\\})");

        ObjectMapper objectMapper = new ObjectMapper();

        MapType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);

        for (String roleString : roleStrings) {
            String[] parts = roleString.split("=", 2);
            String roleName = parts[0];
            String capabilitiesString = parts[1];

            Map<String, Object> capabilities = objectMapper.readValue(capabilitiesString, mapType);

            return capabilities;
        }
        return null; // return null if no capabilities found
    }

    public Map<String, Object> getCapabilitiesByName(String roleName) throws JsonProcessingException {
        if (capabilities != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            MapType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);

            String capabilitiesString = capabilities.toString();
            Map<String, Object> allCapabilities = objectMapper.readValue(capabilitiesString, mapType);

            if (allCapabilities.containsKey(roleName)) {
                return (Map<String, Object>) allCapabilities.get(roleName);
            }
        }
        return null; // Role not found or capabilities are null
    }
}
