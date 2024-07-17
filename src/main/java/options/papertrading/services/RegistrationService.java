package options.papertrading.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.facade.interfaces.IAuthFacade;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.validators.PasswordValidatorService;
import options.papertrading.util.validators.PersonValidator;
import options.papertrading.util.validators.TestIqValidator;
import options.papertrading.util.validators.TestYourIq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService implements IAuthFacade {
    private final PersonsRepository personsRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmtpMailSender smtpMailSender;
    private final TestIqValidator testIqValidator;
    private final PersonValidator personValidator;
    private final TextConverter textConverter;
    private final PasswordValidatorService passwordValidatorService;

    @Value("${pathToMailText}")
    private String pathToMailText;

    public void validate(@NonNull Person person, @NonNull Errors errors) {
        personValidator.validate(person, errors);
    }

    public boolean validate(String password) {
        return passwordValidatorService.isPasswordStrong(password);
    }

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
        }
        catch (IOException exception) {
            throw new RuntimeException("Ошибка при обращении к файлу с текстом письма: " + exception.getMessage());
        }

        try {
            smtpMailSender.sendHtml(person.getEmail(), "Activation code", message);
        }
        catch (MessagingException exception) {
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

    public void testYourIqValidate(TestYourIq testYourIq, BindingResult bindingResult) {
        testIqValidator.validate(testYourIq, bindingResult);
    }

    public TestYourIq testYourIq() {
        Random random = new Random();
        int number1 = random.nextInt(100) + 20;
        int number2 = number1 + random.nextInt(100);
        log.info("{} + {} = {}", number1, number2, number1 + number2);
        return new TestYourIq(number1, number2, number2 * 100 / number1);
    }
}