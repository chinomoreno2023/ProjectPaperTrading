package options.papertrading.util.validators;

import lombok.AllArgsConstructor;
import options.papertrading.models.person.Person;
import options.papertrading.services.PersonsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class PersonValidator implements Validator {
    private final PersonsService personsService;
    private final PasswordValidatorService passwordValidatorService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (personsService.findByEmail(((Person) target).getEmail()).isPresent())
            errors.rejectValue("email", "",
                    "Пользователь с таким email уже существует");

        if (!passwordValidatorService.isPasswordStrong(((Person) target).getPassword()))
            errors.rejectValue("password", "",
                    "Пароль должен быть не менее восьми символов с хотя бы одной цифрой");
    }
}