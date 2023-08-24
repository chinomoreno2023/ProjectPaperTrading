package options.papertrading.util.mappers;

import options.papertrading.dto.PersonDto;
import options.papertrading.models.users.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PersonMapper {
    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
    Person convertToPerson(PersonDto personDto);
    PersonDto convertToPersonDto(Person person);
}
