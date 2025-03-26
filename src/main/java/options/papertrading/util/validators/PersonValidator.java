package options.papertrading.util.validators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.models.TestYourIq;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonValidator implements Validator {
    private final PersonsRepository personsRepository;
    private final PasswordValidatorService passwordValidatorService;
    private final TestIqValidator testIqValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (personsRepository.findByEmail(((Person) target).getEmail()).isPresent())
            errors.rejectValue("email", "",
                    "Пользователь с таким email уже существует");

        if (!validate(((Person) target).getPassword()))
            errors.rejectValue("password", "",
                    "Пароль должен быть не менее восьми символов с хотя бы одной цифрой");
    }

    public boolean validate(String password) {
        return passwordValidatorService.isPasswordStrong(password);
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