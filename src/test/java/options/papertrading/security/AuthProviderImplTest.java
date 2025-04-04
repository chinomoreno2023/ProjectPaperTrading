package options.papertrading.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthProviderImplTest {

    @Mock
    private UserDetailsService personDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthProviderImpl authProvider;

    @Test
    void authenticate_shouldAuthenticateSuccessfully_whenCredentialsAreValid() {
        String username = "user@example.com";
        String rawPassword = "correctPassword";
        String encodedPassword = "encodedPassword";
        UserDetails userDetails = mock(UserDetails.class);

        when(personDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, rawPassword);

        Authentication result = authProvider.authenticate(authentication);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result instanceof UsernamePasswordAuthenticationToken),
                () -> verify(personDetailsService).loadUserByUsername(username),
                () -> verify(passwordEncoder).matches(rawPassword, encodedPassword)
        );
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenPasswordIsIncorrect() {
        String username = "user@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";
        UserDetails userDetails = mock(UserDetails.class);

        when(personDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, rawPassword);

        assertAll(
                () -> assertThrows(BadCredentialsException.class, () -> authProvider.authenticate(authentication)),
                () -> verify(personDetailsService).loadUserByUsername(username),
                () -> verify(passwordEncoder).matches(rawPassword, encodedPassword)
        );
    }

    @Test
    void supports_shouldReturnTrue_whenClassIsUsernamePasswordAuthenticationToken() {
        assertTrue(authProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void supports_shouldReturnFalse_whenClassIsNotUsernamePasswordAuthenticationToken() {
        assertFalse(authProvider.supports(Authentication.class));
    }
}
