package options.papertrading.util.mappers;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

@Mapper(componentModel = "spring")
class PersonMapperTest {
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);

    @Test
    void convertToPerson() {
        PersonDto personDto = new PersonDto();
        personDto.setUsername("username");
        personDto.setEmail("email");
        personDto.setRole("role");

        Person person = personMapper.convertToPerson(personDto);

        assertAll(
                () -> assertNotNull(person),
                () -> assertEquals("username", person.getUsername()),
                () -> assertEquals("email", person.getEmail()),
                () -> assertEquals("role", person.getRole()),
                () -> assertEquals(0.0, person.getId(), 0.001),
                () -> assertNull(person.getPassword()),
                () -> assertEquals(0.0, person.getCurrentNetPosition(), 0.001),
                () -> assertEquals(0.0, person.getOpenLimit(), 0.001),
                () -> assertNull(person.getActivationCode()),
                () -> assertFalse(person.isActive()),
                () -> assertNull(person.getJournal()),
                () -> assertNull(person.getOptionsInPortfolio())
        );
    }

    @Test
    void convertToPerson_shouldReturnNullAndDoesNotThrow_whenInputIsNull() {
        PersonDto personDto = null;

        Person person = personMapper.convertToPerson(personDto);

        assertNull(person);
    }

    @Test
    void convertToPersonDto() {
        Person person = new Person();
        person.setId(1L);
        person.setUsername("username");
        person.setPassword("password");
        person.setEmail("email");
        person.setRole("role");
        person.setCurrentNetPosition(10.0);
        person.setOpenLimit(10.0);
        person.setOptionsInPortfolio(Collections.emptyList());
        person.setJournal(Collections.emptyList());
        person.setActivationCode("activationCode");
        person.setActive(true);

        PersonDto personDto = personMapper.convertToPersonDto(person);

        assertAll(
                () -> assertNotNull(personDto),
                () -> assertEquals("username", personDto.getUsername()),
                () -> assertEquals("email", personDto.getEmail()),
                () -> assertEquals("role", personDto.getRole())
        );
    }

    @Test
    void convertToPersonDto_shouldReturnNullAndDoesNotThrow_whenInputIsNull() {
        Person person = null;

        PersonDto personDto = personMapper.convertToPersonDto(person);

        assertNull(personDto);
    }
}