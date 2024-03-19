package options.papertrading.facade.json;

import lombok.AllArgsConstructor;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IJsonPersonFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class JsonPersonFacade {
    private final IJsonPersonFacade personsService;

    public List<PersonDto> getPersons() {
        return personsService.getPersons();
    }

    public void create(PersonDto personDto) {
        personsService.create(personDto);
    }
}
