package options.papertrading.services;

import lombok.AllArgsConstructor;
import options.papertrading.models.users.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.mail.SmtpMailSender;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final PersonsRepository personsRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmtpMailSender smtpMailSender;

    @Transactional
    @Modifying
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        person.setOpenLimit(1000000);
        person.setActive(false);
        person.setActivationCode(UUID.randomUUID().toString());
        personsRepository.save(person);
        String message = String.format(
                "Hi, %s!<br>Welcome to the OptionsPaperTrading learning project! Please confirm your mail: " +
                        "<a href='http://localhost:8080/auth/activate/%s'>Confirm</a>",
                person.getUsername(), person.getActivationCode());
        try {
            smtpMailSender.sendHtml(person.getEmail(), "Activation code", message);
        }
        catch (MessagingException exception) { exception.printStackTrace(); }
    }

    @Transactional
    @Modifying
    public boolean activatePerson(String code) {
        Person person = personsRepository.findByActivationCode(code);
        if (person == null) {
            return false;
        }
        person.setActive(true);
        person.setActivationCode(null);
        personsRepository.save(person);
        return true;
    }
}