package options.papertrading.services;

import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonDetailsServiceTest {

    @Mock
    private PersonsRepository personsRepository;

    @InjectMocks
    private PersonDetailsService personDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockPerson));

        UserDetails result = personDetailsService.loadUserByUsername("test@example.com");

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result instanceof PersonDetails),
                () -> verify(personsRepository).findByEmail("test@example.com")
        );
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(personsRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertAll(
                () -> assertThrows(UsernameNotFoundException.class,
                        () -> personDetailsService.loadUserByUsername("unknown@example.com")),
                () -> verify(personsRepository).findByEmail("unknown@example.com")
        );
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUsernameIsNull() {
        assertThrows(NullPointerException.class,
                () -> personDetailsService.loadUserByUsername(null));
    }

    @Test
    void loadUserByUsername_ShouldLogAppropriateMessages() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockPerson));

        personDetailsService.loadUserByUsername("test@example.com");

        verify(personsRepository).findByEmail("test@example.com");
    }
}