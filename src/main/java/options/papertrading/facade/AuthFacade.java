package options.papertrading.facade;

import lombok.AllArgsConstructor;
import options.papertrading.facade.interfaces.IAuthFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.validators.TestYourIq;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

@AllArgsConstructor
@Component
public class AuthFacade {
    private final IAuthFacade registrationService;

    public void validate(Person person, Errors errors) {
        registrationService.validate(person, errors);
    }

    public boolean validate(String password) {
        return registrationService.validate(password);
    }

    public void register(Person person) {
        registrationService.register(person);
    }

    public boolean activatePerson(String code) {
        return registrationService.activatePerson(code);
    }

    public void testYourIqValidate(TestYourIq testYourIq, BindingResult bindingResult) {
        registrationService.testYourIqValidate(testYourIq, bindingResult);
    }

    public TestYourIq testYourIq() {
        return registrationService.testYourIq();
    }
}