package options.papertrading.services;

import jakarta.mail.MessagingException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacade;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.mappers.PersonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonsService implements IPersonFacade {
    private final PersonsRepository personsRepository;
    private final PersonMapper personMapper;
    private final SmtpMailSender smtpMailSender;
    private final PasswordEncoder passwordEncoder;
    private final TextConverter textConverter;

    @Value("${pathToPasswordResetText}")
    private String pathToPasswordResetText;

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
        personsRepository.save(convertToPerson(personDto));
    }

    @Transactional
    public void save(@NonNull Person person) {
        log.info("Saving {} to DB", person);
        personsRepository.save(person);
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
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        log.info("Current person is {}", personDetails.getPerson());
        final long id = personDetails.getPerson().getId();
        return personsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PersonDto> getPersons() {
        return findAll().stream()
                        .map(this::convertToPersonDto)
                        .collect(Collectors.toList());
    }

    @Transactional
    public Person updateActivationCode(@NonNull String email) {
        Optional<Person> optional = findByEmail(email);
        if (optional.isEmpty()) {
            throw new NullPointerException();
        }

        Person person = optional.get();
        log.info("Person: {}", person);
        person.setActivationCode(UUID.randomUUID().toString());
        personsRepository.updateActivationCodeByEmail(email, person.getActivationCode());
        return person;
    }

    public void sendMailForResetPassword(@NonNull Person person) throws IOException, MessagingException {
        log.info("Person: {}", person);
        String message = String.format(textConverter.readMailTextFromFile(pathToPasswordResetText),
                                       person.getUsername(),
                                       person.getActivationCode());
        sendMail(person.getEmail(), "Восстановление пароля", message);
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

    public void sendMail(String email, String subject, String message) throws MessagingException {
        log.info("Sending '{}' message with '{}' subject to '{}' email", message, subject, email);
        smtpMailSender.sendHtml(email, subject, message);
    }
}