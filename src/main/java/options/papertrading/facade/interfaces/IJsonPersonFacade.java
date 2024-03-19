package options.papertrading.facade.interfaces;

import options.papertrading.dto.person.PersonDto;

import java.util.List;

public interface IJsonPersonFacade {
    List<PersonDto> getPersons();

    void create(PersonDto personDto);
}
