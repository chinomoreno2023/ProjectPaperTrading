package options.papertrading.facade;

import lombok.AllArgsConstructor;
import options.papertrading.models.person.Person;
import options.papertrading.services.RegistrationService;
import options.papertrading.util.TestYourIq;
import options.papertrading.util.validators.PersonValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

@AllArgsConstructor
@Component
public class AuthFacade {
    private final PersonValidator personValidator;
    private final RegistrationService registrationService;

    public void validate(Person person, Errors errors) {
        personValidator.validate(person, errors);
    }

    public void register(Person person) {
        registrationService.register(person);
    }

    public boolean activatePerson(String code) {
        return registrationService.activatePerson(code);
    }

    public void testYourIqValidate(TestYourIq testYourIq, BindingResult bindingResult) {
        registrationService.validateTestYourIq(testYourIq, bindingResult);
    }

    public TestYourIq testYourIq() {
        return registrationService.testYourIq();
    }
}