package options.papertrading.services;

import jakarta.mail.MessagingException;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private PersonsRepository personsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SmtpMailSender smtpMailSender;

    @Mock
    private TextConverter textConverter;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registerMethod_ShouldBeTransactional() throws NoSuchMethodException {
        Method method = RegistrationService.class.getMethod("register", Person.class);
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);
        assertNotNull(transactional);
    }

    @Test
    void activatePersonMethod_ShouldBeTransactional() throws NoSuchMethodException {
        Method method = RegistrationService.class.getMethod("activatePerson", String.class);
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);
        assertNotNull(transactional);
    }

    @Test
    void register_ShouldSavePersonAndSendEmail() throws IOException, MessagingException {
        Person person = new Person();
        person.setEmail("test@example.com");
        person.setPassword("password");
        person.setUsername("user");

        String encodedPassword = "encodedPassword";
        String mailText = "Mail text";

        when(passwordEncoder.encode("password")).thenReturn(encodedPassword);
        when(textConverter.readMailTextFromFile(null)).thenReturn(mailText);

        registrationService.register(person);

        assertAll(
                () -> assertEquals(encodedPassword, person.getPassword()),
                () -> assertEquals("ROLE_USER", person.getRole()),
                () -> assertEquals(1000000, person.getOpenLimit()),
                () -> assertFalse(person.isActive()),
                () -> assertNotNull(person.getActivationCode()),
                () -> verify(personsRepository).save(person),
                () -> verify(smtpMailSender).sendHtml(
                        eq("test@example.com"),
                        eq("Activation code"),
                        eq(mailText))
        );
    }

    @Test
    void register_ShouldThrow_WhenPersonIsNull() {
        assertThrows(NullPointerException.class, () -> registrationService.register(null));
    }

    @Test
    void activatePerson_ShouldReturnTrue_WhenCodeValid() {
        Person person = new Person();
        when(personsRepository.findByActivationCode("code")).thenReturn(person);

        assertTrue(registrationService.activatePerson("code"));
        assertTrue(person.isActive());
    }

    @Test
    void activatePerson_ShouldThrow_WhenCodeIsNull() {
        assertThrows(NullPointerException.class, () -> registrationService.activatePerson(null));
    }

    @Test
    void class_ShouldHaveServiceAnnotation() {
        Service annotation = AnnotationUtils.findAnnotation(
                RegistrationService.class, Service.class);
        assertNotNull(annotation);
    }
}