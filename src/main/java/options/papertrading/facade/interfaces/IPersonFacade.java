package options.papertrading.facade.interfaces;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import java.util.List;

public interface IPersonFacade {
    List<PersonDto> getPersons();
    void create(PersonDto personDto);
    void updatePassword(Person person, String password);
}
