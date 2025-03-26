package options.papertrading.services;

import jakarta.mail.MessagingException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.facade.interfaces.IAuthFacadeHtmlVersion;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService implements IAuthFacadeHtmlVersion {
    private final PersonsRepository personsRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmtpMailSender smtpMailSender;
    private final TextConverter textConverter;

    @Value("${pathToMailText}")
    private String pathToMailText;

    @Transactional
    public void register(@NonNull Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        person.setOpenLimit(1000000);
        person.setActive(false);
        person.setActivationCode(UUID.randomUUID().toString());
        log.info("New person: {}", person);
        personsRepository.save(person);

        String message;
        try {
            message = String.format(textConverter.readMailTextFromFile(pathToMailText),
                                    person.getUsername(),
                                    person.getActivationCode());
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при обращении к файлу с текстом письма: " + exception.getMessage());
        }

        try {
            smtpMailSender.sendHtml(person.getEmail(), "Activation code", message);
        } catch (MessagingException exception) {
            throw new RuntimeException("Произошла ошибка отправки письма: " + exception.getMessage());
        }
    }

    @Transactional
    public boolean activatePerson(@NonNull String code) {
        Person person = personsRepository.findByActivationCode(code);
        if (person == null) {
            return false;
        }

        person.setActive(true);
        personsRepository.save(person);
        return true;
    }
}