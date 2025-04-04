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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyingIfPortfolioIsNotContainedTest {

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

    @Captor
    private ArgumentCaptor<TradeCreatedEventDto> eventCaptor;

    @InjectMocks
    private BuyingIfPortfolioIsNotContained strategy;

    @Test
    void getType_ShouldReturnStrategyType() {
        assertEquals(AdditionStrategyType.BUYING_IF_NOT_CONTAINED, strategy.getType());
    }

    @Test
    void whenEnoughMoney_thenSavesPortfolioAndSendsEvent() {
        OptionDto optionDto = new OptionDto();
        Portfolio portfolio = mock(Portfolio.class);

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(portfolio);
        when(positionSetter.isThereEnoughMoneyForBuy(portfolio)).thenReturn(true);
        when(portfolio.getOption()).thenReturn(mock(Option.class));
        when(portfolio.getOwner()).thenReturn(mock(Person.class));

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertNull(event.getPortfolioForDelete()),
                () -> assertEquals(portfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(portfolio, optionDto),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                                BuyingIfPortfolioIsNotContained.class.getMethod("addPortfolio", OptionDto.class),
                                Transactional.class))
        );
    }

    @Test
    void addPortfolio_ShouldThrowException_WhenNotEnoughMoney() {
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(portfolio);
        when(positionSetter.isThereEnoughMoneyForBuy(portfolio)).thenReturn(false);

        assertThrows(InsufficientFundsException.class, () -> strategy.addPortfolio(optionDto));

        assertAll(
                () -> verify(portfolio, never()).setTradePrice(anyDouble()),
                () -> verify(producerService, never()).createEvent(any()),
                () -> verify(journalService, never()).addJournal(any(), any())
        );
    }

    @Test
    void addPortfolio_ShouldThrowException_WhenOptionDtoIsNull() {
        assertThrows(NullPointerException.class,
                () -> strategy.addPortfolio(null));
    }
}