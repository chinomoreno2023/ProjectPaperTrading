package options.papertrading.util.converters;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import options.papertrading.util.mappers.PersonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonConverterTest {

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonConverter personConverter;

    @Test
    void convertToPerson() {
        when(personMapper.convertToPerson(any(PersonDto.class))).thenReturn(new Person());

        Person person = personConverter.convertToPerson(new PersonDto());

        assertAll(
                () -> verify(personMapper, times(1)).convertToPerson(any(PersonDto.class)),
                () -> assertNotNull(person)
        );
    }

    @Test
    void convertToPerson_DoesNotThrow_whenInputIsNull() {
        PersonDto personDto = null;

        assertThrows(NullPointerException.class,
                () -> personConverter.convertToPerson(personDto));
    }

    @Test
    void convertToPersonDto() {
        when(personMapper.convertToPersonDto(any(Person.class))).thenReturn(new PersonDto());

        PersonDto personDto = personConverter.convertToPersonDto(new Person());

        assertAll(
                () -> verify(personMapper, times(1)).convertToPersonDto(any(Person.class)),
                () -> assertNotNull(personDto)
        );
    }

    @Test
    void convertToPersonDto_ShouldThrow_whenInputIsNull() {
        Person person = null;

        assertThrows(NullPointerException.class,
                () -> personConverter.convertToPersonDto(person));
    }
}