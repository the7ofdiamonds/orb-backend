package tech.orbfin.api.gateway.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import tech.orbfin.api.gateway.repositories.IRepositoryUserDetails;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@RequiredArgsConstructor
@Data
public class Capabilities {
    private final IRepositoryUserDetails iRepositoryUserDetails;

    private Map<String, Map<String, Boolean>> roles;

    public Map<String, Map<String, Boolean>> getRoles() {
//        IRepositoryUserDetails iRepositoryUserDetails = new IRepositoryUserDetails();
        return iRepositoryUserDetails.getRoles();
    }

    public Map<String, Boolean> getCapabilitiesForRole(String role) {
        return roles.getOrDefault(role, Map.of());
    }
}
