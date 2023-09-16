package options.papertrading.services;

import lombok.AllArgsConstructor;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import options.papertrading.util.mappers.PersonMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PersonsService {
    private final PersonsRepository personsRepository;
    private final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByEmail(String email) {
        return personsRepository.findByEmail(email);
    }

    @Transactional
    @Modifying
    public void create(PersonDto personDto) {
        personsRepository.save(convertToPerson(personDto));
    }


    public Person convertToPerson(PersonDto personDto) {
        return personMapper.convertToPerson(personDto);
    }

    public PersonDto convertToPersonDto(Person person) {
        return personMapper.convertToPersonDto(person);
    }

    public PersonNotCreatedException showPersonError(BindingResult bindingResult) {
        StringBuilder errorMessage = null;
        if (bindingResult.hasErrors()) {
            errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
        }
        return new PersonNotCreatedException(errorMessage.toString());
    }

    @Transactional(readOnly = true)
    public Person getCurrentPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return personDetails.getPerson();
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getPersons() {
        return findAll().stream()
                        .map(this::convertToPersonDto)
                        .collect(Collectors.toList());
    }
}