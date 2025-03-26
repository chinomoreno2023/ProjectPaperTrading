package options.papertrading.facade;

import lombok.RequiredArgsConstructor;
import options.papertrading.facade.interfaces.IAuthFacadeHtmlVersion;
import options.papertrading.models.TestYourIq;
import options.papertrading.models.person.Person;
import options.papertrading.util.validators.PersonValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Component
public class AuthFacade {
    private final IAuthFacadeHtmlVersion registrationService;
    private final PersonValidator personValidator;

    public void validate(Person person, Errors errors) {
        personValidator.validate(person, errors);
    }

    public boolean validate(String password) {
        return personValidator.validate(password);
    }

    public void register(Person person) {
        registrationService.register(person);
    }

    public boolean activatePerson(String code) {
        return registrationService.activatePerson(code);
    }

    public void testYourIqValidate(TestYourIq testYourIq, BindingResult bindingResult) {
        personValidator.testYourIqValidate(testYourIq, bindingResult);
    }

    public TestYourIq testYourIq() {
        return personValidator.testYourIq();
    }
}