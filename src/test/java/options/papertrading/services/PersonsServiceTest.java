package options.papertrading.services;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PersonsServiceTest {

    @Autowired
    private PersonsService personsService;

    private final String username = "user";
    private final String password = "123";
    private final String email = "user@mail.ru";
    private final String role = "ROLE_USER";
    private final double currentNetPosition = 100.0;
    private final double openLimit = 50.0;
    private final List<Portfolio> optionsInPortfolio = Collections.emptyList();
    private final String activationCode = "777";
    private final boolean isActive = true;

    @Test
    void findAll() {
        List<Person> personList = personsService.findAll();

        assertThat(personList).isNotNull();
        assertThat(personList.size()).isNotEqualTo(0);
        personList.forEach(person -> assertThat(person).isInstanceOf(Person.class));
    }

    @Test
    void findByEmail() {
        String email = "admin@mail.ru";
        Person person = personsService.findByEmail(email).get();

        assertThat(person).isNotNull();
        assertThat(person.getUsername()).isEqualTo("admin");
        assertThat(person.getCurrentNetPosition()).isInstanceOf(Double.class);
        assertThat(person.getOpenLimit()).isInstanceOf(Double.class);
        assertThat(person.getPassword()).isInstanceOf(String.class);
        assertThat(person.getRole()).isEqualTo("ROLE_ADMIN");
        assertThat(person.getActivationCode()).isInstanceOf(String.class);
        assertThat(person.isActive()).isEqualTo(isActive);
    }

    @Test
    void convertToPerson() {
        PersonDto personDto = new PersonDto();
        personDto.setUsername(username);
        personDto.setEmail(email);
        personDto.setRole(role);
        Person person = personsService.convertToPerson(personDto);

        assertThat(person).isNotNull();
        assertThat(person.getUsername()).isEqualTo(username);
        assertThat(person.getEmail()).isEqualTo(email);
        assertThat(person.getCurrentNetPosition()).isInstanceOf(Double.class);
        assertThat(person.getOpenLimit()).isInstanceOf(Double.class);
        assertThat(person.getPassword()).isEqualTo(null);
        assertThat(person.getRole()).isEqualTo(role);
        assertThat(person.getActivationCode()).isEqualTo(null);
        assertThat(person.isActive()).isInstanceOf(Boolean.class);
    }

    @Test
    void convertToPersonDto() {
        Person person = new Person();
        person.setUsername(username);
        person.setPassword(password);
        person.setEmail(email);
        person.setRole(role);
        person.setCurrentNetPosition(currentNetPosition);
        person.setOpenLimit(openLimit);
        person.setOptionsInPortfolio(optionsInPortfolio);
        person.setActivationCode(activationCode);
        person.setActive(isActive);
        PersonDto personDto = personsService.convertToPersonDto(person);

        assertThat(personDto).isNotNull();
        assertThat(personDto).isInstanceOf(PersonDto.class);
        assertThat(personDto.getUsername()).isEqualTo(username);
        assertThat(personDto.getEmail()).isEqualTo(email);
        assertThat(personDto.getRole()).isEqualTo(role);
    }

    @Test
    void getPersons() {
        List<PersonDto> personList = personsService.getPersons();

        assertThat(personList).isNotNull();
        assertThat(personList.size()).isNotEqualTo(0);
        personList.forEach(personDto -> assertThat(personDto).isInstanceOf(PersonDto.class));
    }
}