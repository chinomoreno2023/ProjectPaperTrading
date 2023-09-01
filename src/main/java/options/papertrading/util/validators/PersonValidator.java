package options.papertrading.util.validators;

import lombok.AllArgsConstructor;
import options.papertrading.models.users.Person;
import options.papertrading.services.PersonsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
public class PersonValidator implements Validator {
    private final PersonsService personsService;;

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (personsService.findByEmail(((Person) target).getEmail()).isPresent())
            errors.rejectValue("email", "", "This email is already taken");
    }
}