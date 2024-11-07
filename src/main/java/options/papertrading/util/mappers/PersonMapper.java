package options.papertrading.util.mappers;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    Person convertToPerson(PersonDto personDto);
    PersonDto convertToPersonDto(Person person);
}
