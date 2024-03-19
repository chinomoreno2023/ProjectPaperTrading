package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IJsonPersonFacade;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import options.papertrading.util.mappers.PersonMapper;
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

@Slf4j
@Service
@AllArgsConstructor
public class PersonsService implements IJsonPersonFacade {
    private final PersonsRepository personsRepository;
    private final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> findByEmail(@NonNull String email) {
        log.info("Finding person by {}", email);
        return personsRepository.findByEmail(email);
    }

    @Transactional
    @Modifying
    public void create(@NonNull PersonDto personDto) {
        log.info("Saving {} to DB", personDto);
        personsRepository.save(convertToPerson(personDto));
    }


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

    public PersonNotCreatedException showPersonError(BindingResult bindingResult) {
        StringBuilder errorMessage = null;
        if (bindingResult.hasErrors()) {
            errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                            .append(" - ")
                            .append(error.getDefaultMessage())
                            .append(";");
            }
        }
        log.info("Person not created. Error: {}", errorMessage.toString());
        return new PersonNotCreatedException(errorMessage.toString());
    }

    @Transactional(readOnly = true)
    public Person getCurrentPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        log.info("Current person is {}", personDetails.getPerson());
        return personDetails.getPerson();
    }

//    @Transactional(readOnly = true)
//    public Person getCurrentPerson() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof PersonDetails) {
//                return ((PersonDetails) principal).getPerson();
//            }
//        }
//        throw new AuthenticationCredentialsNotFoundException("User not authenticated");
//    }

    @Transactional(readOnly = true)
    public List<PersonDto> getPersons() {
        return findAll().stream()
                        .map(this::convertToPersonDto)
                        .collect(Collectors.toList());
    }
}