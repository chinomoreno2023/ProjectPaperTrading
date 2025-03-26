package options.papertrading.facade;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.person.Person;
import options.papertrading.util.mail.DefaultMailSender;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PersonFacade {
    private final IPersonFacadeHtmlVersion personFacade;
    private final DefaultMailSender mailSender;

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
        mailSender.sendMail(myMail, subject, message);
    }

    public Person updateActivationCode(String email) {
        return personFacade.updateActivationCode(email);
    }

    public void sendMailForResetPassword(Person person) throws MessagingException, IOException {
        mailSender.sendMailForResetPassword(person);
    }

    public void save(Person person) {
        personFacade.save(person);
    }
}