package options.papertrading.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import options.papertrading.util.converters.PersonConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonsService implements IPersonFacadeHtmlVersion {
    private final PersonsRepository personsRepository;
    private final PersonConverter personConverter;
    private final PasswordEncoder passwordEncoder;
    private final BeanFactory beanFactory;

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
    public void create(@NonNull PersonDto personDto) {
        log.info("Saving {} to DB", personDto);
        personsRepository.save(personConverter.convertToPerson(personDto));
    }

    @Transactional
    public Person save(@NonNull Person person) {
        log.info("Saving {} to DB", person);
        personsRepository.save(person);
        return person;
    }

    @Transactional(readOnly = true)
    public Person getCurrentPerson() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        log.info("Current person is {}", personDetails.getPerson());
        final long id = personDetails.getPerson().getId();
        return getPersonById(id);
    }

    public Person getPersonById(long id) {
        return personsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getPersons() {
        return beanFactory.getBean(PersonsService.class)
                .findAll().parallelStream()
                .map(personConverter::convertToPersonDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Person updateActivationCode(@NonNull String email) {
        Optional<Person> optional = beanFactory.getBean(PersonsService.class)
                .findByEmail(email);
        if (optional.isEmpty()) {
            throw new NullPointerException();
        }

        Person person = optional.get();
        log.info("Person: {}", person);
        person.setActivationCode(UUID.randomUUID().toString());
        personsRepository.updateActivationCodeByEmail(email, person.getActivationCode());
        return person;
    }

    @Transactional(readOnly = true)
    public Person findByActivationCode(@NonNull String code) {
        Person person = personsRepository.findByActivationCode(code);
        log.info("Person: {}", person);
        return person;
    }

    @Transactional
    public void updatePassword(@NonNull Person person, @NonNull String password) {
        log.info("Person: {}", person);
        person.setPassword(passwordEncoder.encode(password));
        person.setActivationCode(null);
        personsRepository.save(person);
    }
}