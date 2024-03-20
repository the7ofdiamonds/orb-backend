package tech.orbfin.api.gateway.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tech.orbfin.api.gateway.MockUser;
import tech.orbfin.api.gateway.model.UserEntity;
import tech.orbfin.api.gateway.model.response.ResponseLogin;
import tech.orbfin.api.gateway.model.wordpress.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServiceAuthLogin {

    @Mock
    private ServiceUserDetails serviceUserDetails;

    @Mock
    private ServiceUserUtils serviceUserUtils;

    @Mock
    private ServiceTokenJW serviceTokenJW;

    @Mock
    private ServiceSession serviceSession;

    @InjectMocks
    private ServiceAuthLogin serviceAuthLogin;

    private MockUser mockUser;

    public TestServiceAuthLogin() {
        MockitoAnnotations.openMocks(this);
        serviceAuthLogin = new ServiceAuthLogin(serviceTokenJW, serviceSession, serviceUserUtils, serviceUserDetails);
    }

    @Test
    public void testSuccessfulLogin() throws Exception {

        User user = MockUser.createMockUser();
        UserEntity mockUser = new UserEntity(user);
        when(serviceUserDetails.setCredentialsNonExpired(anyString(), anyString())).thenReturn(mockUser);
        when(serviceUserUtils.validateAccount(mockUser)).thenReturn(true);
        when(ServiceTokenJW.generateToken(anyMap(), eq(mockUser))).thenReturn("mockAccessToken");
        when(serviceTokenJW.refreshToken(mockUser)).thenReturn("mockRefreshToken");
        when(serviceSession.createSession(anyString(), anyString(), anyString())).thenReturn(true);

        ResponseLogin response = serviceAuthLogin.login("testUser", "testPassword", new Object());

        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        assertEquals("mockAccessToken", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
    }

    @Test
    public void testInvalidCredentials() {
        try {
            when(serviceUserDetails.setCredentialsNonExpired(anyString(), anyString())).thenReturn(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertThrows(IllegalArgumentException.class, () -> serviceAuthLogin.login("invalidUser", "invalidPassword", new Object()));
    }
}
