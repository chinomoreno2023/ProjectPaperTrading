package options.papertrading.services;

import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PersonDetailsServiceDiffblueTest {
    /**
     * Method under test: {@link PersonDetailsService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        Optional<Person> ofResult = Optional.of(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        UserDetails actualLoadUserByUsernameResult = (new PersonDetailsService(personsRepository))
                .loadUserByUsername("janedoe");

        // Assert
        verify(personsRepository).findByEmail(eq("janedoe"));
        Collection<? extends GrantedAuthority> authorities = actualLoadUserByUsernameResult.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities instanceof List);
        assertTrue(actualLoadUserByUsernameResult instanceof PersonDetails);
        GrantedAuthority getResult = ((List<? extends GrantedAuthority>) authorities).get(0);
        assertTrue(getResult instanceof SimpleGrantedAuthority);
        assertEquals("Role", getResult.toString());
        assertEquals("Role", getResult.getAuthority());
        assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
        assertEquals("jane.doe@example.org", actualLoadUserByUsernameResult.getUsername());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
        assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
        assertTrue(actualLoadUserByUsernameResult.isCredentialsNonExpired());
        assertTrue(actualLoadUserByUsernameResult.isEnabled());
        assertSame(person, ((PersonDetails) actualLoadUserByUsernameResult).getPerson());
    }

    /**
     * Method under test: {@link PersonDetailsService#loadUserByUsername(String)}
     */
    @Test
    void testLoadUserByUsername2() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        Optional<Person> emptyResult = Optional.empty();
        when(personsRepository.findByEmail(Mockito.<String>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(UsernameNotFoundException.class,
                () -> (new PersonDetailsService(personsRepository)).loadUserByUsername("janedoe"));
        verify(personsRepository).findByEmail(eq("janedoe"));
    }
}
