package options.papertrading.facade;

import lombok.AllArgsConstructor;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacade;
import options.papertrading.models.person.Person;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class PersonFacade {
    private final IPersonFacade personFacade;

    public List<PersonDto> getPersons() {
        return personFacade.getPersons();
    }

    public void create(PersonDto personDto) {
        personFacade.create(personDto);
    }

    public Person findByActivationCode(String code) {
        return personFacade.findByActivationCode(code);
    }

    public void updatePassword(Person person, String password) {
        personFacade.updatePassword(person, password);
    }

    public Person getCurrentPerson() {
        return personFacade.getCurrentPerson();
    }

    public void sendMail(String myMail, String subject, String message) throws MessagingException {
        personFacade.sendMail(myMail, subject, message);
    }

    public Person updateActivationCode(String email) {
        return personFacade.updateActivationCode(email);
    }

    public void sendMailForResetPassword(Person person) throws MessagingException, IOException {
        personFacade.sendMailForResetPassword(person);
    }

    public void save(Person person) {
        personFacade.save(person);
    }
}