package options.papertrading.facade.interfaces;

import options.papertrading.models.person.Person;

public interface IPersonFacadeHtmlVersion extends IPersonFacade {
    Person updateActivationCode(String email);
    Person findByActivationCode(String code);
    Person getCurrentPerson();
    Person save(Person person);
}
