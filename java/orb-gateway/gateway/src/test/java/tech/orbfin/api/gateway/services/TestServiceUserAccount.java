package tech.orbfin.api.gateway.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.UserCreationException;
import tech.orbfin.api.gateway.model.UserEntity;
import tech.orbfin.api.gateway.model.response.ResponseRegister;
import tech.orbfin.api.gateway.model.response.ResponseUnlocked;
import tech.orbfin.api.gateway.model.response.ResponseRemoveAccount;
import tech.orbfin.api.gateway.model.wordpress.User;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserAccount;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserUtils;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServiceUserAccount {

    @Mock
    private IRepositoryUserAccount iRepositoryUserAccount;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ServiceUserFirebase serviceUserFirebase;

    @Mock
    private ServiceUserDetails serviceUserDetails;
    @Mock
    private IRepositoryUserUtils iRepositoryUserUtils;
    @Mock
    private ServiceUserUtils serviceUserUtils;

    @InjectMocks
    private ServiceUserAccount serviceUserAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        serviceUserUtils = new ServiceUserUtils(iRepositoryUserUtils, serviceUserFirebase, kafkaTemplate);
    }

//    @Test
//    void testRegisterAccount_Success() throws Exception {
//        // Mocking required behaviors
//        when(serviceUserUtils.validateEmail(anyString())).thenReturn(true);
//        when(serviceUserUtils.validUsername(anyString())).thenReturn(true);
//        when(serviceUserUtils.userExist(anyString(), anyString(), anyString())).thenReturn(false);
//        when(serviceUserUtils.passwordsMatch(anyString(), anyString())).thenReturn(true);
//        when(serviceUserUtils.validName(anyString())).thenReturn(true);
//        when(serviceUserFirebase.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(null);
//        when(iRepositoryUserAccount.signupUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyString())).thenReturn(Optional.of(createTestUser()));
//        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(null);
//
//        // Call the method under test
//        ResponseRegister response = serviceUserAccount.registerAccount("test@example.com", "testuser", "1Password$", "1Password$", "John", "Doe", "+1234567890", null);
//
//        // Assertions
//        assertNotNull(response);
//        assertEquals("testuser", response.getUsername());
////        assertEquals("test@example.com", response.getEmail());
//    }

//    @Test
//    void testRegisterAccount_InvalidEmail() throws Exception {
//        // Mocking required behaviors
//        when(serviceUserUtils.validEmail(anyString())).thenReturn(false);
//
//        // Call the method under test and assert that it throws the expected exception
//        assertThrows(BadCredentialsException.class, () -> serviceUserAccount.registerAccount("invalid-email", "testuser", "password", "password", "John", "Doe", "1234567890", null));
//    }
//
//    // Add more test cases for other scenarios
//
//    private User createTestUser() {
//        User user = new User();
//        user.setUsername("testuser");
//        user.setEmail("test@example.com");
//        // Set other properties as needed
//        return user;
//    }
}

