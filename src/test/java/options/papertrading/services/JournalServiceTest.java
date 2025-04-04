package options.papertrading.services;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.JournalConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private IPersonFacadeHtmlVersion personsService;

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private JournalConverter journalConverter;

    @Mock
    private Portfolio portfolio;

    @Mock
    private OptionDto optionDto;

    @Mock
    private Person person;

    @InjectMocks
    private JournalService journalService;

    @Test
    void showJournal_ShouldReturnJournalDtoList() {
        Person mockPerson = mock(Person.class);
        Journal mockJournal = mock(Journal.class);
        JournalDto mockJournalDto = mock(JournalDto.class);

        when(personsService.getCurrentPerson()).thenReturn(mockPerson);
        when(journalRepository.findAllByOwner(mockPerson))
                .thenReturn(Collections.singletonList(mockJournal));
        when(journalConverter.convertJournalListToJournalDtoList(anyList()))
                .thenReturn(Collections.singletonList(mockJournalDto));

        List<JournalDto> result = journalService.showJournal();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockJournalDto, result.get(0)),
                () -> verify(personsService).getCurrentPerson(),
                () -> verify(journalRepository).findAllByOwner(mockPerson),
                () -> verify(journalConverter).convertJournalListToJournalDtoList(anyList())
        );
    }

    @Test
    void addJournal_ShouldSaveJournalWithCorrectParameters() {
        double testPrice = 100.0;
        int testVolume = 5;
        String testOptionName = "OPTION1";
        int testBuyOrWrite = 1;

        when(portfolio.getOption()).thenReturn(mock(options.papertrading.models.option.Option.class));
        when(portfolio.getOption().getId()).thenReturn(testOptionName);
        when(portfolio.getOwner()).thenReturn(person);
        when(optionDto.getVolume()).thenReturn(testVolume);
        when(optionDto.getBuyOrWrite()).thenReturn(testBuyOrWrite);
        when(positionSetter.countPriceInRur(portfolio)).thenReturn(testPrice);

        journalService.addJournal(portfolio, optionDto);

        verify(journalRepository).save(argThat(journal ->
                journal.getOptionName().equals(testOptionName) &&
                        journal.getVolume() == testVolume &&
                        journal.getPrice() == testPrice &&
                        journal.getBuyOrWrite() == testBuyOrWrite &&
                        journal.getOwner() == person
        ));
    }

    @Test
    void clearJournal_ShouldDeleteAllByOwner() {
        Person mockPerson = mock(Person.class);
        when(personsService.getCurrentPerson()).thenReturn(mockPerson);

        journalService.clearJournal();

        verify(journalRepository).deleteAllByOwner(mockPerson);
    }

    @Test
    void addJournal_ShouldThrowException_WhenPortfolioIsNull() {
        assertThrows(NullPointerException.class,
                () -> journalService.addJournal(null, optionDto));
    }

    @Test
    void addJournal_ShouldThrowException_WhenOptionDtoIsNull() {
        assertThrows(NullPointerException.class,
                () -> journalService.addJournal(portfolio, null));
    }

    @Test
    void showJournalMethod_ShouldBeTransactionalReadOnly() throws NoSuchMethodException {
        Method method = JournalService.class.getMethod("showJournal");
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);

        assertAll(
                () -> assertNotNull(transactional),
                () -> assertTrue(transactional.readOnly())
        );
    }

    @Test
    void addJournalMethod_ShouldBeTransactional() throws NoSuchMethodException {
        Method method = JournalService.class.getMethod("addJournal", Portfolio.class, OptionDto.class);
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);

        assertAll(
                () -> assertNotNull(transactional),
                () -> assertFalse(transactional.readOnly())
        );
    }

    @Test
    void clearJournalMethod_ShouldBeTransactional() throws NoSuchMethodException {
        Method method = JournalService.class.getMethod("clearJournal");
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);

        assertAll(
                () -> assertNotNull(transactional),
                () -> assertFalse(transactional.readOnly())
        );
    }
}