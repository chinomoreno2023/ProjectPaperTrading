package options.papertrading.util;

import options.papertrading.dao.PersonDao;
import options.papertrading.models.users.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {
    private final PersonDao personDao;

    @Autowired
    public PersonValidator(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (personDao.show(((Person) target).getEmail()).isPresent())
            errors.rejectValue("email", "", "This email is already taken");
        if (personDao.showName(((Person) target).getName()).isPresent())
            errors.rejectValue("name", "","This name is already taken");
    }
}
