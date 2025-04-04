package options.papertrading.services.strategies;

import kafka.producer.model.dto.TradeCreatedEventDto;
import kafka.producer.service.ProducerService;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.PortfolioConverter;
import options.papertrading.util.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WritingIfPortfolioIsNotContainedTest {

    @Mock
    private PortfolioConverter portfolioConverter;

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private ProducerService producerService;

    @Mock
    private IJournalFacade journalService;

    @Mock
    private Portfolio portfolio;

    @Mock
    private OptionDto optionDto;

    @InjectMocks
    private WritingIfPortfolioIsNotContained strategy;

    @Test
    void getType_ShouldReturnStrategyType() {
        assertEquals(AdditionStrategyType.WRITING_IF_NOT_CONTAINED, strategy.getType());
    }

    @Test
    void whenEnoughMoney_thenCreatesPortfolioAndUpdatesPosition() {
        Person mockPerson = mock(Person.class);
        Option mockOption = mock(Option.class);
        Portfolio portfolio = mock(Portfolio.class);

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(portfolio);
        when(portfolio.getOption()).thenReturn(mockOption);
        when(portfolio.getOwner()).thenReturn(mockPerson);
        when(optionDto.getVolume()).thenReturn(5);
        when(mockOption.getWriteCollateral()).thenReturn(100.0);
        when(positionSetter.isThereEnoughMoneyForWrite(portfolio)).thenReturn(true);
        when(positionSetter.countPriceInRur(portfolio)).thenReturn(100.0);

        strategy.addPortfolio(optionDto);

        ArgumentCaptor<Double> netPositionCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> openLimitCaptor = ArgumentCaptor.forClass(Double.class);

        assertAll(
                () -> verify(mockPerson).setCurrentNetPosition(netPositionCaptor.capture()),
                () -> verify(mockPerson).setOpenLimit(openLimitCaptor.capture())
        );

        ArgumentCaptor<TradeCreatedEventDto> eventCaptor = ArgumentCaptor.forClass(TradeCreatedEventDto.class);
        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(portfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(portfolio, optionDto),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        WritingIfPortfolioIsNotContained.class.getMethod("addPortfolio", OptionDto.class),
                        Transactional.class))
        );
    }

    @Test
    void whenNotEnoughMoney_thenThrowsInsufficientFundsException() {
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(portfolio);
        when(positionSetter.isThereEnoughMoneyForWrite(portfolio)).thenReturn(false);

        assertThrows(InsufficientFundsException.class, () -> strategy.addPortfolio(optionDto));

        verify(portfolio, never()).setVolume(anyInt());
        verify(producerService, never()).createEvent(any());
        verify(journalService, never()).addJournal(any(), any());
    }

    @Test
    void whenOptionDtoIsNull_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> strategy.addPortfolio(null));
    }
}