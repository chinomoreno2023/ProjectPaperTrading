package options.papertrading.util.validators;

import options.papertrading.models.TestYourIq;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonValidatorTest {

    @Mock
    private PersonsRepository personsRepository;

    @Mock
    private PasswordValidatorService passwordValidatorService;

    @Mock
    private TestIqValidator testIqValidator;

    @InjectMocks
    private PersonValidator personValidator;

    @Test
    void supports() {
        assertTrue(personValidator.supports(Person.class));
    }

    @Test
    void supports_ShouldReturnFalse() {
        assertFalse(personValidator.supports(Option.class));
    }

    @Test
    void supports_ShouldReturnFalse_OnNull() {
        assertFalse(personValidator.supports(null));
    }

    @Test
    void validate() {
        when(passwordValidatorService.isPasswordStrong(anyString())).thenReturn(true);

        personValidator.validate("dhfhefwkljefwo");

        verify(passwordValidatorService).isPasswordStrong(anyString());
    }

    @Test
    void validate_shouldAddError_whenEmailAlreadyExists() {
        Person person = new Person();
        person.setEmail("to@example.com");
        person.setPassword("validPass1");

        Errors errors = new BeanPropertyBindingResult(person, "person");
        when(personsRepository.findByEmail(person.getEmail())).thenReturn(Optional.of(person));

        personValidator.validate(person, errors);

        assertAll (
                () -> assertTrue(errors.hasErrors()),
                () -> assertNotNull(errors.getFieldError("email")),
                () -> assertEquals("Пользователь с таким email уже существует",
                        errors.getFieldError("email").getDefaultMessage()),
                () -> verify(personsRepository).findByEmail(person.getEmail())
        );
    }

    @Test
    void validate_shouldAddError_whenPasswordIsInvalid() {
        Person person = new Person();
        person.setEmail("new@example.com");
        person.setPassword("short");

        Errors errors = new BeanPropertyBindingResult(person, "person");
        when(personsRepository.findByEmail(person.getEmail())).thenReturn(Optional.empty());

        personValidator.validate(person, errors);

        assertAll(
                () -> assertTrue(errors.hasErrors()),
                () -> assertNotNull(errors.getFieldError("password")),
                () -> assertEquals("Пароль должен быть не менее восьми символов с хотя бы одной цифрой",
                        errors.getFieldError("password").getDefaultMessage())
                );
    }

    @Test
    void validate_shouldNotAddErrors_whenDataIsValid() {
        Person person = new Person();
        person.setEmail("new@example.com");
        person.setPassword("djfh1dgshefg");

        Errors errors = new BeanPropertyBindingResult(person, "person");
        when(personsRepository.findByEmail(person.getEmail())).thenReturn(Optional.empty());
        when(passwordValidatorService.isPasswordStrong(anyString())).thenReturn(true);

        personValidator.validate(person, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_shouldAddBothErrors_whenEmailExistsAndPasswordInvalid() {
        Person person = new Person();
        person.setEmail("existing@example.com");
        person.setPassword("invalid");

        Errors errors = new BeanPropertyBindingResult(person, "person");
        when(personsRepository.findByEmail(person.getEmail())).thenReturn(Optional.of(person));

        personValidator.validate(person, errors);

        assertAll(
                () -> assertTrue(errors.hasErrors()),
                () -> assertNotNull(errors.getFieldError("email")),
                () -> assertNotNull(errors.getFieldError("password")),
                () -> assertEquals(2, errors.getErrorCount())
        );
    }

    @Test
    void testYourIqValidate_shouldDelegateToTestIqValidator() {
        TestYourIq testYourIq = new TestYourIq(1, 2, 3);
        BindingResult bindingResult = mock(BindingResult.class);

        doNothing().when(testIqValidator).validate(testYourIq, bindingResult);

        personValidator.testYourIqValidate(testYourIq, bindingResult);

        assertAll(
                () -> verify(testIqValidator, times(1))
                        .validate(testYourIq, bindingResult),
                () -> verifyNoMoreInteractions(testIqValidator)
        );
    }

    @Test
    void testYourIq_shouldAlwaysReturnValidData() {
        for (int i = 0; i < 10; i++) {
            TestYourIq testYourIq = personValidator.testYourIq();
            int expectedResult = testYourIq.getNumber2() * 100 / testYourIq.getNumber1();

            assertAll(
                    () -> assertTrue(testYourIq.getNumber1() >= 20),
                    () -> assertTrue(testYourIq.getNumber1() <= 119),
                    () -> assertTrue(testYourIq.getNumber2() > testYourIq.getNumber1()),
                    () -> assertEquals(expectedResult, testYourIq.getResult())
            );
        }
    }
}