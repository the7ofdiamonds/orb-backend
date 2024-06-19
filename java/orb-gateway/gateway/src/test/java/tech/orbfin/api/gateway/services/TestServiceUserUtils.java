package tech.orbfin.api.gateway.services;

import tech.orbfin.api.gateway.exceptions.BadCredentialsException;
import tech.orbfin.api.gateway.exceptions.ExceptionMessages;
import tech.orbfin.api.gateway.model.wordpress.repositories.IRepositoryUserUtils;
import tech.orbfin.api.gateway.services.firebase.ServiceUserFirebase;

import tech.orbfin.api.gateway.services.user.ServiceUserUtils;
import tech.orbfin.api.gateway.utils.Patterns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServiceUserUtils {
    @Mock
    private IRepositoryUserUtils repositoryUserUtils;

    @Mock
    private ServiceUserFirebase serviceUserFirebase;

    private ServiceUserUtils serviceUserUtils;

    @Mock
    private IRepositoryUserUtils iRepositoryUserUtils;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        serviceUserUtils = new ServiceUserUtils(iRepositoryUserUtils, serviceUserFirebase, kafkaTemplate);
    }

    @Test
    void testValidEmail_WithValidEmail_ShouldReturnTrue() {
        // Arrange
        String validEmail = "test@example.com";

        // Act
        boolean result = serviceUserUtils.validEmail(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidEmail_WithNullEmail_ShouldThrowBadCredentialsException() {
        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validEmail(null);
        });
    }

    @Test
    void testValidEmail_WithInvalidEmailFormat_ShouldThrowBadCredentialsException() {
        // Arrange
        String invalidEmail = "invalid_email";

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validEmail(invalidEmail);
        });
    }

    @Test
    void testValidEmail_WithEmptyEmail_ShouldThrowBadCredentialsException() {
        // Arrange
        String emptyEmail = "";

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validEmail(emptyEmail);
        });
    }

    @Test
    void testValidUsername_WithValidUsername_ShouldReturnTrue() {
        // Arrange
        String validUsername = "user123";

        // Act
        boolean result = serviceUserUtils.validUsername(validUsername);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidUsername_WithNullUsername_ShouldThrowBadCredentialsException() {
        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validUsername(null);
        });
    }

    @Test
    void testValidUsername_WithTooLongUsername_ShouldThrowBadCredentialsException() {
        // Arrange
        String longUsername = "a".repeat(Patterns.USERNAME_MAX_LENGTH + 1);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validUsername(longUsername);
        });
    }

    @Test
    void testValidUsername_WithInvalidUsernameFormat_ShouldThrowBadCredentialsException() {
        // Arrange
        String invalidUsername = "user@name";

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validUsername(invalidUsername);
        });
    }

    @Test
    void testValidUsername_WithEmptyUsername_ShouldThrowBadCredentialsException() {
        // Arrange
        String emptyUsername = "";

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validUsername(emptyUsername);
        });
    }

    @Test
    void testValidPassword_WithValidPassword_ShouldReturnTrue() {
        // Arrange
        String validPassword = "$Password123";

        // Act
        boolean result = serviceUserUtils.validPassword(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidPassword_WithNullPassword_ShouldThrowBadCredentialsException() {
        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validPassword(null);
        });
    }

    @Test
    void testValidPassword_WithTooLongPassword_ShouldThrowBadCredentialsException() {
        // Arrange
        String longPassword = "a".repeat(Patterns.PASSWORD_MAX_LENGTH + 1);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validPassword(longPassword);
        });
    }

    @Test
    void testValidPassword_WithInvalidPasswordFormat_ShouldThrowBadCredentialsException() {
        String invalidPassword = "l^Pssword9";

        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validPassword(invalidPassword);
        });
    }

    @Test
    void testValidPassword_WithEmptyPassword_ShouldThrowBadCredentialsException() {
        // Arrange
        String emptyPassword = "";

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            serviceUserUtils.validPassword(emptyPassword);
        });
    }

    @Test
    void testValidPhone_Valid() {
        // Call the method under test with a valid phone number
        assert serviceUserUtils.validPhone("+1234567890");
    }

    @Test
    void testValidPhone_Null() {
        // Call the method under test with a null phone number and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validPhone(null));
    }

    @Test
    void testValidPhone_TooLong() {
        // Call the method under test with a phone number longer than the maximum allowed length and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validPhone("12345678901234567890"));
    }

    @Test
    void testValidPhone_InvalidFormat() {
        // Call the method under test with an invalid phone number format and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validPhone("invalidphone"));
    }

    @Test
    void testValidName_Valid() {
        // Call the method under test with a valid name
        assert serviceUserUtils.validName("John");
    }

    @Test
    void testValidName_Null() {
        // Call the method under test with a null name and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validName(null));
    }

    @Test
    void testValidName_TooLong() {
        // Call the method under test with a name longer than the maximum allowed length and assert that it throws BadCredentialsException
        String longName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam gravida nisi in ex posuere, vel pharetra velit ultricies.";
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validName(longName));
    }

    @Test
    void testValidName_InvalidFormat() {
        // Call the method under test with an invalid name format and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validName("Invalid!Name"));
    }

    @Test
    void testValidateEmail_Valid() {
        when(iRepositoryUserUtils.existsByEmail(anyString())).thenReturn(true);
        try {
            when(serviceUserFirebase.userExistByEmail(anyString())).thenReturn(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assert serviceUserUtils.validateEmail("jamel.c.lyons@gmail.com");
    }

    @Test
    void testValidateEmail_NotValid() {
        // Call the method under test with an invalid email and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validateEmail("invalid_email"));
    }

    @Test
    void testValidateEmail_NotFound() {
        // Stub the repository and service methods
        when(iRepositoryUserUtils.existsByEmail(anyString())).thenReturn(false);
//        when(serviceUserFirebase.userExistByEmail(anyString())).thenReturn(false);

        // Call the method under test with an email that is not found and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validateEmail("notfound@example.com"));

        // Verify that KafkaTemplate send method is called with the correct topic and email
//        verify(kafkaTemplate, times(1)).send(ConfigKafkaTopics.PASSWORD_RECOVERY, "notfound@example.com");
    }

    @Test
    void testValidateUsername_Valid() throws Exception {
        // Stub the repository method to return true
        when(iRepositoryUserUtils.existsByUsername(anyString())).thenReturn(true);

        // Call the method under test with a valid username
        assert serviceUserUtils.validateUsername("masteradmin");
    }

    @Test
    void testValidateUsername_NotValid() {
        // Call the method under test with an invalid username and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validateUsername("invalid!username"));
    }

    @Test
    void testValidateUsername_NotFound() {
        // Stub the repository method to return false
        when(iRepositoryUserUtils.existsByUsername(anyString())).thenReturn(false);

        // Call the method under test with a username that is not found and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.validateUsername("notfound_username"));

        // Verify that the exception message is correct
        try {
            serviceUserUtils.validateUsername("notfound_username");
        } catch (BadCredentialsException e) {
            assertEquals(ExceptionMessages.USERNAME_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPasswordsMatch_Valid() throws Exception {
        // Call the method under test with valid passwords
        assertTrue(serviceUserUtils.passwordsMatch("$Password123", "$Password123"));
    }

    @Test
    void testPasswordsMatch_Invalid() {
        // Call the method under test with invalid passwords and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.passwordsMatch("password123", "wrongpassword"));
    }

    @Test
    void testPasswordsMatch_NullConfirmPassword() {
        // Call the method under test with a null confirm password and assert that it throws BadCredentialsException
        assertThrows(BadCredentialsException.class, () -> serviceUserUtils.passwordsMatch("password123", null));

        // Verify that the exception message is correct
        try {
            serviceUserUtils.passwordsMatch("$Password123", null);
        } catch (BadCredentialsException e) {
            assertEquals(ExceptionMessages.PASSWORD_CONFIRM_NULL, e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
