package options.papertrading.facade.interfaces;

import options.papertrading.models.person.Person;
import options.papertrading.util.validators.TestYourIq;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

public interface IAuthFacade {
    void validate(Person person, Errors errors);
    boolean validate(String password);
    void register(Person person);
    boolean activatePerson(String code);
    void testYourIqValidate(TestYourIq testYourIq, BindingResult bindingResult);
    TestYourIq testYourIq();
}