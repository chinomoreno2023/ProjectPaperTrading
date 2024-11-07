package options.papertrading.facade.interfaces;

import jakarta.mail.MessagingException;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;

import java.io.IOException;
import java.util.List;

public interface IPersonFacade {
    List<PersonDto> getPersons();
    void create(PersonDto personDto);
    Person updateActivationCode(String email);
    void sendMailForResetPassword(Person person) throws IOException, MessagingException;
    Person findByActivationCode(String code);
    void updatePassword(Person person, String password);
    Person getCurrentPerson();
    void sendMail(String email, String subject, String message) throws MessagingException;
    void save(Person person);
}