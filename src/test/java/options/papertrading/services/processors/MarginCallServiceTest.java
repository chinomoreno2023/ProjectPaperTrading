package options.papertrading.services.processors;

import jakarta.mail.MessagingException;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.converters.PortfolioConverter;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarginCallServiceTest {

    @Mock
    private IPersonFacadeHtmlVersion personsService;

    @Mock
    private PortfoliosRepository portfoliosRepository;

    @Mock
    private PortfolioConverter portfolioConverter;

    @Mock
    private IJournalFacade journalService;

    @Mock
    private SmtpMailSender smtpMailSender;

    @Mock
    private TextConverter textConverter;

    @Mock
    private PortfolioManager portfolioManager;

    @InjectMocks
    private MarginCallService marginCallService;

    private Person person;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() throws IOException {
        when(textConverter.readMailTextFromFile(any())).thenReturn("Hello, %s!");
        person = mock(Person.class);
        portfolio = mock(Portfolio.class);
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(person.getOpenLimit()).thenReturn(100.0);
        when(person.getCurrentNetPosition()).thenReturn(200.0);
        when(portfoliosRepository.findAllByOwner(person)).thenReturn(List.of(portfolio));
    }

    @Test
    void marginCallCheck_shouldSendMail_whenPortfolioIsEmpty() throws MessagingException {
        when(person.getOpenLimit()).thenReturn(50.0);
        when(person.getCurrentNetPosition()).thenReturn(200.0);
        when(portfoliosRepository.findAllByOwner(person)).thenReturn(List.of());

        marginCallService.marginCallCheck();

        assertAll(
                () -> verify(smtpMailSender, times(1)).sendHtml(any(), any(), any()),
                () -> assertTrue(AnnotationUtils.findAnnotation(
                        MarginCallService.class.getMethod("marginCallCheck"),
                        Transactional.class) != null)

        );
        verify(smtpMailSender, times(1)).sendHtml(any(), any(), any());
    }


    @Test
    void marginCallCheck_shouldProcessPortfolio_whenLimitIsLow() {
        Portfolio portfolio = mock(Portfolio.class);

        when(person.getOpenLimit()).thenReturn(50.0);
        when(person.getCurrentNetPosition()).thenReturn(200.0);
        when(portfoliosRepository.findAllByOwner(person)).thenReturn(List.of(portfolio));

        when(portfolio.getVolume()).thenReturn(2, 1, 0);
        when(portfolio.getVariatMargin()).thenReturn(10.0);
        when(portfolioConverter.convertPortfolioToOptionDto(any())).thenReturn(new OptionDto());

        marginCallService.marginCallCheck();

        verify(journalService, atLeastOnce()).addJournal(any(), any());
    }

    @Test
    void sendMarginCallMail_shouldThrowException_whenEmailFails() throws IOException, MessagingException {
        when(textConverter.readMailTextFromFile(any())).thenReturn("Test Message");
        doThrow(new MessagingException("Email error")).when(smtpMailSender).sendHtml(any(), any(), any());

        when(person.getOpenLimit()).thenReturn(50.0);
        when(person.getCurrentNetPosition()).thenReturn(200.0);
        when(portfoliosRepository.findAllByOwner(person)).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            marginCallService.marginCallCheck();
        });

        assertEquals("Ошибка при отправке почты", exception.getMessage());
    }
}
