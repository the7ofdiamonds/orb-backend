package tech.orbfin.api.gateway;

import tech.orbfin.api.gateway.model.wordpress.User;

public class MockUser {

    public static User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setPhone("1234567890");
        user.setProviderGivenID("provider123");
        user.setConfirmationCode("confirm123");
        user.setRoles("ROLE_USER");
        user.setIsAuthenticated(true);
        user.setIsAccountNonExpired(true);
        user.setIsAccountNonLocked(true);
        user.setIsCredentialsNonExpired(true);
        user.setIsEnabled(true);
        return user;
    }
}
