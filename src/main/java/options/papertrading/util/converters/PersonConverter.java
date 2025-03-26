package options.papertrading.util.converters;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import options.papertrading.util.mappers.PersonMapper;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PersonConverter {
    private final PersonMapper personMapper;

    public Person convertToPerson(@NonNull PersonDto personDto) {
        Person person = personMapper.convertToPerson(personDto);
        log.info("Converting {} to person. Result: {}", personDto, person);
        return person;
    }

    public PersonDto convertToPersonDto(@NonNull Person person) {
        PersonDto personDto = personMapper.convertToPersonDto(person);
        log.info("Converting {} to personsDto. Result: {}", person, personDto);
        return personDto;
    }
}
