package options.papertrading.services;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.mappers.JournalMapper;
import options.papertrading.util.mappers.PersonMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class JournalServiceDiffblueTest {
    /**
     * Method under test: {@link JournalService#showJournal()}
     */
    @Test
    void testShowJournal() {
        // Arrange
        JournalRepository journalRepository = mock(JournalRepository.class);
        when(journalRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(new ArrayList<>());

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
        PersonsService personsService = mock(PersonsService.class);
        when(personsService.getCurrentPerson()).thenReturn(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<JournalDto> actualShowJournalResult = (new JournalService(JournalMapper.INSTANCE, journalRepository,
                personsService, new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE,
                smtpMailSender, passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).showJournal();

        // Assert
        verify(journalRepository).findAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
        assertTrue(actualShowJournalResult.isEmpty());
    }

    /**
     * Method under test: {@link JournalService#showJournal()}
     */
    @Test
    void testShowJournal2() {
        // Arrange
        Person owner = new Person();
        owner.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        journal.setDateAndTime(ofResult.atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        ArrayList<Journal> journalList = new ArrayList<>();
        journalList.add(journal);
        JournalRepository journalRepository = mock(JournalRepository.class);
        when(journalRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(journalList);

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
        PersonsService personsService = mock(PersonsService.class);
        when(personsService.getCurrentPerson()).thenReturn(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<JournalDto> actualShowJournalResult = (new JournalService(JournalMapper.INSTANCE, journalRepository,
                personsService, new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE,
                smtpMailSender, passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).showJournal();

        // Assert
        verify(journalRepository).findAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
        assertEquals(1, actualShowJournalResult.size());
        JournalDto getResult = actualShowJournalResult.get(0);
        LocalDateTime dateAndTime = getResult.getDateAndTime();
        assertEquals("00:00", dateAndTime.toLocalTime().toString());
        LocalDate toLocalDateResult = dateAndTime.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Converting journal list '{}' to journalDto list. Result: {} ", getResult.getOptionName());
        assertEquals(1, getResult.getVolume());
        assertEquals(10.0d, getResult.getPrice());
        assertEquals(19088743, getResult.getBuyOrWrite());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Method under test: {@link JournalService#showJournal()}
     */
    @Test
    void testShowJournal3() {
        // Arrange
        Person owner = new Person();
        owner.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        journal.setDateAndTime(ofResult.atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        Person owner2 = new Person();
        owner2.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(0.5d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(0.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Converting {} to journalDto. Result: {}");
        owner2.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner2.setUsername("Converting {} to journalDto. Result: {}");

        Journal journal2 = new Journal();
        journal2.setBuyOrWrite(1);
        LocalDate ofResult2 = LocalDate.of(1970, 1, 1);
        journal2.setDateAndTime(ofResult2.atStartOfDay());
        journal2.setId(2L);
        journal2.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal2.setOwner(owner2);
        journal2.setPrice(0.5d);
        journal2.setVolume(0);

        ArrayList<Journal> journalList = new ArrayList<>();
        journalList.add(journal2);
        journalList.add(journal);
        JournalRepository journalRepository = mock(JournalRepository.class);
        when(journalRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(journalList);

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
        PersonsService personsService = mock(PersonsService.class);
        when(personsService.getCurrentPerson()).thenReturn(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        List<JournalDto> actualShowJournalResult = (new JournalService(JournalMapper.INSTANCE, journalRepository,
                personsService, new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE,
                smtpMailSender, passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).showJournal();

        // Assert
        verify(journalRepository).findAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
        assertEquals(2, actualShowJournalResult.size());
        JournalDto getResult = actualShowJournalResult.get(0);
        LocalDateTime dateAndTime = getResult.getDateAndTime();
        LocalTime toLocalTimeResult = dateAndTime.toLocalTime();
        assertEquals("00:00", toLocalTimeResult.toString());
        LocalDate toLocalDateResult = dateAndTime.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        JournalDto getResult2 = actualShowJournalResult.get(1);
        LocalDateTime dateAndTime2 = getResult2.getDateAndTime();
        LocalDate toLocalDateResult2 = dateAndTime2.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult2.toString());
        assertEquals("Converting journal list '{}' to journalDto list. Result: {} ", getResult.getOptionName());
        assertEquals("Converting journal list '{}' to journalDto list. Result: {} ", getResult2.getOptionName());
        assertEquals(0, getResult.getVolume());
        assertEquals(0.5d, getResult.getPrice());
        assertEquals(1, getResult.getBuyOrWrite());
        assertEquals(1, getResult2.getVolume());
        assertEquals(10.0d, getResult2.getPrice());
        assertEquals(19088743, getResult2.getBuyOrWrite());
        assertSame(toLocalTimeResult, dateAndTime2.toLocalTime());
        assertSame(ofResult2, toLocalDateResult);
        assertSame(ofResult, toLocalDateResult2);
    }

    /**
     * Method under test: {@link JournalService#convertJournalToJournalDto(Journal)}
     */
    @Test
    void testConvertJournalToJournalDto() {
        // Arrange
        JournalRepository journalRepository = mock(JournalRepository.class);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        PersonsRepository personsRepository2 = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender2 = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder2 = new Argon2PasswordEncoder();
        JournalService journalService = new JournalService(JournalMapper.INSTANCE, journalRepository, personsService,
                new PositionSetter(null, new PersonsService(personsRepository2, PersonMapper.INSTANCE, smtpMailSender2,
                        passwordEncoder2, new TextConverter()), mock(PortfoliosRepository.class)));

        Person owner = new Person();
        owner.setActivationCode("Activation Code");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Role");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        journal.setDateAndTime(ofResult.atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Option Name");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        // Act
        JournalDto actualConvertJournalToJournalDtoResult = journalService.convertJournalToJournalDto(journal);

        // Assert
        LocalDateTime dateAndTime = actualConvertJournalToJournalDtoResult.getDateAndTime();
        assertEquals("00:00", dateAndTime.toLocalTime().toString());
        LocalDate toLocalDateResult = dateAndTime.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Option Name", actualConvertJournalToJournalDtoResult.getOptionName());
        assertEquals(1, actualConvertJournalToJournalDtoResult.getVolume());
        assertEquals(10.0d, actualConvertJournalToJournalDtoResult.getPrice());
        assertEquals(19088743, actualConvertJournalToJournalDtoResult.getBuyOrWrite());
        assertSame(dateAndTime, journal.getDateAndTime());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Method under test:
     * {@link JournalService#convertJournalDtoToJournal(JournalDto)}
     */
    @Test
    void testConvertJournalDtoToJournal() {
        // Arrange
        JournalRepository journalRepository = mock(JournalRepository.class);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        PersonsRepository personsRepository2 = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender2 = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder2 = new Argon2PasswordEncoder();
        JournalService journalService = new JournalService(JournalMapper.INSTANCE, journalRepository, personsService,
                new PositionSetter(null, new PersonsService(personsRepository2, PersonMapper.INSTANCE, smtpMailSender2,
                        passwordEncoder2, new TextConverter()), mock(PortfoliosRepository.class)));

        JournalDto journalDto = new JournalDto();
        journalDto.setBuyOrWrite(19088743);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        journalDto.setDateAndTime(ofResult.atStartOfDay());
        journalDto.setOptionName("Option Name");
        journalDto.setPrice(10.0d);
        journalDto.setVolume(1);

        // Act
        Journal actualConvertJournalDtoToJournalResult = journalService.convertJournalDtoToJournal(journalDto);

        // Assert
        LocalDateTime dateAndTime = actualConvertJournalDtoToJournalResult.getDateAndTime();
        assertEquals("00:00", dateAndTime.toLocalTime().toString());
        LocalDate toLocalDateResult = dateAndTime.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Option Name", actualConvertJournalDtoToJournalResult.getOptionName());
        assertNull(actualConvertJournalDtoToJournalResult.getOwner());
        assertEquals(0L, actualConvertJournalDtoToJournalResult.getId());
        assertEquals(1, actualConvertJournalDtoToJournalResult.getVolume());
        assertEquals(10.0d, actualConvertJournalDtoToJournalResult.getPrice());
        assertEquals(19088743, actualConvertJournalDtoToJournalResult.getBuyOrWrite());
        assertSame(dateAndTime, journalDto.getDateAndTime());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Method under test:
     * {@link JournalService#convertJournalListToJournalDtoList(List)}
     */
    @Test
    void testConvertJournalListToJournalDtoList() {
        // Arrange
        JournalRepository journalRepository = mock(JournalRepository.class);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        PersonsRepository personsRepository2 = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender2 = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder2 = new Argon2PasswordEncoder();
        JournalService journalService = new JournalService(JournalMapper.INSTANCE, journalRepository, personsService,
                new PositionSetter(null, new PersonsService(personsRepository2, PersonMapper.INSTANCE, smtpMailSender2,
                        passwordEncoder2, new TextConverter()), mock(PortfoliosRepository.class)));

        // Act and Assert
        assertTrue(journalService.convertJournalListToJournalDtoList(new ArrayList<>()).isEmpty());
    }

    /**
     * Method under test: {@link JournalService#addJournal(Portfolio, OptionDto)}
     */
    @Test
    void testAddJournal() {
        // Arrange
        Person owner = new Person();
        owner.setActivationCode("Activation Code");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Role");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        journal.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Option Name");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);
        JournalRepository journalRepository = mock(JournalRepository.class);
        when(journalRepository.save(Mockito.<Journal>any())).thenReturn(journal);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        PersonsService personsService = new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                passwordEncoder, new TextConverter());

        PersonsRepository personsRepository2 = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender2 = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder2 = new Argon2PasswordEncoder();
        JournalService journalService = new JournalService(JournalMapper.INSTANCE, journalRepository, personsService,
                new PositionSetter(null, new PersonsService(personsRepository2, PersonMapper.INSTANCE, smtpMailSender2,
                        passwordEncoder2, new TextConverter()), mock(PortfoliosRepository.class)));

        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Type");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

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
        Portfolio portfolio = mock(Portfolio.class);
        when(portfolio.getOwner()).thenReturn(person);
        when(portfolio.getOption()).thenReturn(option);
        OptionDto optionDto = mock(OptionDto.class);
        when(optionDto.getBuyOrWrite()).thenReturn(19088743);
        when(optionDto.getVolume()).thenReturn(1);

        // Act
        journalService.addJournal(portfolio, optionDto);

        // Assert
        verify(optionDto).getBuyOrWrite();
        verify(optionDto).getVolume();
        verify(portfolio, atLeast(1)).getOption();
        verify(portfolio).getOwner();
        verify(journalRepository).save(isA(Journal.class));
    }

    /**
     * Method under test: {@link JournalService#clearJournal()}
     */
    @Test
    void testClearJournal() {
        // Arrange
        JournalRepository journalRepository = mock(JournalRepository.class);
        doNothing().when(journalRepository).deleteAllByOwner(Mockito.<Person>any());

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
        PersonsService personsService = mock(PersonsService.class);
        when(personsService.getCurrentPerson()).thenReturn(person);
        PersonsRepository personsRepository = mock(PersonsRepository.class);
        SmtpMailSender smtpMailSender = new SmtpMailSender(new JavaMailSenderImpl());
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

        // Act
        (new JournalService(JournalMapper.INSTANCE, journalRepository, personsService,
                new PositionSetter(null, new PersonsService(personsRepository, PersonMapper.INSTANCE, smtpMailSender,
                        passwordEncoder, new TextConverter()), mock(PortfoliosRepository.class)))).clearJournal();

        // Assert
        verify(journalRepository).deleteAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
    }
}
