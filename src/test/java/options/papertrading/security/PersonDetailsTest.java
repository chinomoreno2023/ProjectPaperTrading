package options.papertrading.security;

import options.papertrading.models.person.Person;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class PersonDetailsTest {

    @Test
    void testUserDetailsMethods() {
        Person person = new Person();
        person.setEmail("user@example.com");
        person.setPassword("securePassword");
        person.setRole("ROLE_USER");
        person.setActive(true);

        PersonDetails personDetails = new PersonDetails(person);

        assertAll(
                () -> assertEquals("user@example.com", personDetails.getUsername()),
                () -> assertEquals("securePassword", personDetails.getPassword()),
                () -> assertTrue(personDetails.isEnabled()),
                () -> assertTrue(personDetails.isAccountNonExpired()),
                () -> assertTrue(personDetails.isAccountNonLocked()),
                () -> assertTrue(personDetails.isCredentialsNonExpired())
        );

        Collection<? extends GrantedAuthority> authorities = personDetails.getAuthorities();

        assertAll(
                () -> assertEquals(1, authorities.size()),
                () -> assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")))
        );
    }
}
