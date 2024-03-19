package options.papertrading.services;

import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.TestYourIq;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.validators.TestIqValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Execution(ExecutionMode.SAME_THREAD)
class RegistrationServiceTest {
    private final PersonsRepository personsRepository;
    private final TestIqValidator testIqValidator;
    private final RegistrationService registrationService;
    private final SmtpMailSender smtpMailSender;
    String username = "testUser";
    String password = "testPassword";
    String email = "trancecore1@gmail.com";

    @Autowired
    RegistrationServiceTest(PersonsRepository personsRepository,
                            TestIqValidator testIqValidator,
                            RegistrationService registrationService,
                            SmtpMailSender smtpMailSender) {
        this.personsRepository = personsRepository;
        this.testIqValidator = testIqValidator;
        this.registrationService = registrationService;
        this.smtpMailSender = smtpMailSender;
    }

    @Test
    void register() {
        Person person = new Person();
        person.setUsername(username);
        person.setPassword(password);
        person.setEmail(email);

        registrationService.register(person);
        Optional<Person> optionalPerson = personsRepository.findByEmail(email);
        Person savedPerson = optionalPerson.get();

        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.getUsername()).isEqualTo(username);
        assertThat(savedPerson.getCurrentNetPosition()).isEqualTo(0.0);
        assertThat(savedPerson.getOpenLimit()).isEqualTo(1000000.0);
        assertThat(savedPerson.getPassword()).isInstanceOf(String.class);
        assertThat(savedPerson.getPassword()).isNotBlank();
        assertThat(savedPerson.getRole()).isEqualTo("ROLE_USER");
        assertThat(savedPerson.getActivationCode()).isInstanceOf(String.class);
        assertThat(savedPerson.getActivationCode()).isNotBlank();
        assertThat(savedPerson.isActive()).isEqualTo(false);

        registrationService.deleteByEmail(email);
    }

    @Test
    void activatePerson() {
        Person person = new Person();
        person.setUsername(username);
        person.setPassword(password);
        person.setEmail(email);

        registrationService.register(person);
        String activationCode = person.getActivationCode();
        registrationService.activatePerson(activationCode);
        Optional<Person> optionalPerson = personsRepository.findByEmail(email);
        Person savedPerson = optionalPerson.get();

        assertThat(savedPerson).isNotNull();
        assertThat(savedPerson.isActive()).isEqualTo(true);

        registrationService.deleteByEmail(email);
    }

    @Test
    void testYourIq() {
        TestYourIq testYourIq = registrationService.testYourIq();

        assertThat(testYourIq).isNotNull();
        assertThat(testYourIq).isInstanceOf(TestYourIq.class);
        assertThat(testYourIq.getNumber1()).isGreaterThan(0);
        assertThat(testYourIq.getNumber2()).isGreaterThan(0);
        assertThat(testYourIq.getResult()).isEqualTo(testYourIq.getNumber2() * 100 / testYourIq.getNumber1());
    }
}