package options.papertrading.services;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.mappers.PersonMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PersonsServiceDiffblueTest {
    /**
     * Method under test: {@link PersonsService#findAll()}
     */
    @Test
    void testFindAll() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        ArrayList<Person> personList = new ArrayList<>();
        when(personsRepository.findAll()).thenReturn(personList);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        // Act
        List<Person> actualFindAllResult = personsService.findAll();

        // Assert
        verify(personsRepository).findAll();
        assertTrue(actualFindAllResult.isEmpty());
        assertEquals(actualFindAllResult, personsService.getPersons());
        assertSame(personList, actualFindAllResult);
    }

    /**
     * Method under test: {@link PersonsService#findByEmail(String)}
     */
    @Test
    void testFindByEmail() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        Optional<Person> ofResult = Optional.of(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        Optional<Person> actualFindByEmailResult = (new PersonsService(personsRepository, PersonMapper.INSTANCE,
                smtpMailSender, passwordEncoder, new TextConverter())).findByEmail("jane.doe@example.org");

        // Assert
        verify(personsRepository).findByEmail(eq("jane.doe@example.org"));
        assertSame(ofResult, actualFindByEmailResult);
    }

    /**
     * Method under test: {@link PersonsService#create(PersonDto)}
     */
    @Test
    void testCreate() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.save(Mockito.<Person>any())).thenReturn(person);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        PersonDto personDto = new PersonDto();
        personDto.setEmail("jane.doe@example.org");
        personDto.setRole("Role");
        personDto.setUsername("janedoe");

        // Act
        personsService.create(personDto);

        // Assert
        verify(personsRepository).save(isA(Person.class));
    }

    /**
     * Method under test: {@link PersonsService#save(Person)}
     */
    @Test
    void testSave() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.save(Mockito.<Person>any())).thenReturn(person);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        Person person2 = new Person();
        person2.setActivationCode("Activation Code");
        person2.setActive(true);
        person2.setCurrentNetPosition(10.0d);
        person2.setEmail("jane.doe@example.org");
        person2.setId(1L);
        person2.setJournal(new ArrayList<>());
        person2.setOpenLimit(10.0d);
        person2.setOptionsInPortfolio(new ArrayList<>());
        person2.setPassword("iloveyou");
        person2.setRole("Role");
        person2.setUsername("janedoe");

        // Act
        personsService.save(person2);

        // Assert
        verify(personsRepository).save(isA(Person.class));
    }

    /**
     * Method under test: {@link PersonsService#convertToPerson(PersonDto)}
     */
    @Test
    void testConvertToPerson() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        PersonDto personDto = new PersonDto();
        personDto.setEmail("jane.doe@example.org");
        personDto.setRole("Role");
        personDto.setUsername("janedoe");

        // Act
        Person actualConvertToPersonResult = personsService.convertToPerson(personDto);

        // Assert
        assertEquals("Role", actualConvertToPersonResult.getRole());
        assertEquals("jane.doe@example.org", actualConvertToPersonResult.getEmail());
        assertEquals("janedoe", actualConvertToPersonResult.getUsername());
        assertNull(actualConvertToPersonResult.getActivationCode());
        assertNull(actualConvertToPersonResult.getPassword());
        assertNull(actualConvertToPersonResult.getJournal());
        assertNull(actualConvertToPersonResult.getOptionsInPortfolio());
        assertEquals(0.0d, actualConvertToPersonResult.getCurrentNetPosition());
        assertEquals(0.0d, actualConvertToPersonResult.getOpenLimit());
        assertEquals(0L, actualConvertToPersonResult.getId());
        assertFalse(actualConvertToPersonResult.isActive());
    }

    /**
     * Method under test: {@link PersonsService#convertToPersonDto(Person)}
     */
    @Test
    void testConvertToPersonDto() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");

        // Act
        PersonDto actualConvertToPersonDtoResult = personsService.convertToPersonDto(person);

        // Assert
        assertEquals("Role", actualConvertToPersonDtoResult.getRole());
        assertEquals("jane.doe@example.org", actualConvertToPersonDtoResult.getEmail());
        assertEquals("janedoe", actualConvertToPersonDtoResult.getUsername());
    }

    /**
     * Method under test: {@link PersonsService#showPersonError(BindingResult)}
     */
    @Test
    void testShowPersonError() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        BindException bindingResult = new BindException("Target", "Object Name");
        bindingResult.addError(new ObjectError("Person not created. Error: {}", "Person not created. Error: {}"));

        // Act
        PersonNotCreatedException actualShowPersonErrorResult = personsService.showPersonError(bindingResult);

        // Assert
        assertEquals("", actualShowPersonErrorResult.getLocalizedMessage());
        assertEquals("", actualShowPersonErrorResult.getMessage());
        assertNull(actualShowPersonErrorResult.getCause());
        Throwable[] suppressed = actualShowPersonErrorResult.getSuppressed();
        assertEquals(0, suppressed.length);
        assertSame(suppressed, bindingResult.getSuppressed());
    }

    /**
     * Method under test: {@link PersonsService#showPersonError(BindingResult)}
     */
    @Test
    void testShowPersonError2() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        BindException bindingResult = new BindException("Target", "Object Name");
        bindingResult.addError(new FieldError("Person not created. Error: {}", "Person not created. Error: {}",
                "Person not created. Error: {}"));

        // Act
        PersonNotCreatedException actualShowPersonErrorResult = personsService.showPersonError(bindingResult);

        // Assert
        assertEquals("Person not created. Error: {} - Person not created. Error: {};",
                actualShowPersonErrorResult.getLocalizedMessage());
        assertEquals("Person not created. Error: {} - Person not created. Error: {};",
                actualShowPersonErrorResult.getMessage());
        assertNull(actualShowPersonErrorResult.getCause());
        Throwable[] suppressed = actualShowPersonErrorResult.getSuppressed();
        assertEquals(0, suppressed.length);
        assertSame(suppressed, bindingResult.getSuppressed());
    }

    /**
     * Method under test: {@link PersonsService#getPersons()}
     */
    @Test
    void testGetPersons() {
        // Arrange
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.findAll()).thenReturn(new ArrayList<>());
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<PersonDto> actualPersons = (new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter())).getPersons();

        // Assert
        verify(personsRepository).findAll();
        assertTrue(actualPersons.isEmpty());
    }

    /**
     * Method under test: {@link PersonsService#getPersons()}
     */
    @Test
    void testGetPersons2() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");

        ArrayList<Person> personList = new ArrayList<>();
        personList.add(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.findAll()).thenReturn(personList);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<PersonDto> actualPersons = (new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter())).getPersons();

        // Assert
        verify(personsRepository).findAll();
        assertEquals(1, actualPersons.size());
        PersonDto getResult = actualPersons.get(0);
        assertEquals("Role", getResult.getRole());
        assertEquals("jane.doe@example.org", getResult.getEmail());
        assertEquals("janedoe", getResult.getUsername());
    }

    /**
     * Method under test: {@link PersonsService#getPersons()}
     */
    @Test
    void testGetPersons3() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");

        Person person2 = new Person();
        person2.setActivationCode("Activation Code");
        person2.setActive(false);
        person2.setCurrentNetPosition(0.5d);
        person2.setEmail("john.smith@example.org");
        person2.setId(2L);
        person2.setJournal(new ArrayList<>());
        person2.setOpenLimit(0.5d);
        person2.setOptionsInPortfolio(new ArrayList<>());
        person2.setPassword("Converting {} to personsDto. Result: {}");
        person2.setRole("Role");
        person2.setUsername("Converting {} to personsDto. Result: {}");

        ArrayList<Person> personList = new ArrayList<>();
        personList.add(person2);
        personList.add(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.findAll()).thenReturn(personList);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<PersonDto> actualPersons = (new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter())).getPersons();

        // Assert
        verify(personsRepository).findAll();
        assertEquals(2, actualPersons.size());
        PersonDto getResult = actualPersons.get(0);
        assertEquals("Converting {} to personsDto. Result: {}", getResult.getUsername());
        assertEquals("Role", getResult.getRole());
        PersonDto getResult2 = actualPersons.get(1);
        assertEquals("Role", getResult2.getRole());
        assertEquals("jane.doe@example.org", getResult2.getEmail());
        assertEquals("janedoe", getResult2.getUsername());
        assertEquals("john.smith@example.org", getResult.getEmail());
    }

    /**
     * Method under test: {@link PersonsService#sendMailForResetPassword(Person)}
     */
    @Test
    void testSendMailForResetPassword() throws IOException, MessagingException {
        // Arrange
        SmtpMailSender smtpMailSender = mock(SmtpMailSender.class);
        doNothing().when(smtpMailSender).sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        TextConverter textConverter = mock(TextConverter.class);
        when(textConverter.readMailTextFromFile(Mockito.<String>any())).thenReturn("jane.doe@example.org");
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                new Argon2PasswordEncoder(), textConverter);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");

        // Act
        personsService.sendMailForResetPassword(person);

        // Assert
        verify(textConverter).readMailTextFromFile(isNull());
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Восстановление пароля"),
                eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link PersonsService#findByActivationCode(String)}
     */
    @Test
    void testFindByActivationCode() {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        when(personsRepository.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        Person actualFindByActivationCodeResult = (new PersonsService(personsRepository, PersonMapper.INSTANCE,
                smtpMailSender, passwordEncoder, new TextConverter())).findByActivationCode("Code");

        // Assert
        verify(personsRepository).findByActivationCode(eq("Code"));
        assertSame(person, actualFindByActivationCodeResult);
    }

    /**
     * Method under test: {@link PersonsService#sendMail(String, String, String)}
     */
    @Test
    void testSendMail() throws MessagingException {
        // Arrange
        SmtpMailSender smtpMailSender = mock(SmtpMailSender.class);
        doNothing().when(smtpMailSender).sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        (new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender, passwordEncoder, new TextConverter()))
                .sendMail("jane.doe@example.org", "Hello from the Dreaming Spires", "Not all who wander are lost");

        // Assert
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Hello from the Dreaming Spires"),
                eq("Not all who wander are lost"));
    }
}
