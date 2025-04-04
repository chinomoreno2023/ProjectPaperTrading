package options.papertrading.services.strategies;

import kafka.producer.model.dto.TradeCreatedEventDto;
import kafka.producer.service.ProducerService;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
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
class DirectBuyingPortfolioIfItIsContainedTest {

    @Mock
    private PortfolioConverter portfolioConverter;

    @Mock
    private PortfoliosRepository portfoliosRepository;

    @Mock
    private IPersonFacadeHtmlVersion personsService;

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private IPortfolioManager portfolioManager;

    @Mock
    private IJournalFacade journalService;

    @Mock
    private ProducerService producerService;

    @Mock
    private Portfolio newPortfolio;

    @Mock
    private Portfolio oldPortfolio;

    @Mock
    private OptionDto optionDto;

    @Mock
    private Option option;

    @Mock
    private Person person;

    @Captor
    private ArgumentCaptor<TradeCreatedEventDto> eventCaptor;

    @InjectMocks
    private DirectBuyingPortfolioIfItIsContained strategy;

    @Test
    void getType_ShouldReturnStrategyType() {
        assertEquals(AdditionStrategyType.DIRECT_BUYING_IF_CONTAINED, strategy.getType());
    }

    @Test
    void whenEnoughMoney_thenUpdatesPortfolioAndSendsEvent() {
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(newPortfolio.getOption()).thenReturn(option);
        when(newPortfolio.getOwner()).thenReturn(person);
        when(optionsRepository.findOneById(any())).thenReturn(option);
        when(portfoliosRepository.findByOwnerAndOption(person, option)).thenReturn(oldPortfolio);
        when(positionSetter.isThereEnoughMoneyForBuy(newPortfolio)).thenReturn(true);
        when(optionDto.getVolume()).thenReturn(1);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(oldPortfolio, event.getPortfolioForDelete()),
                () -> assertEquals(newPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        DirectBuyingPortfolioIfItIsContained.class.getMethod("addPortfolio", OptionDto.class),
                        Transactional.class))
        );
    }

    @Test
    void whenNotEnoughMoney_thenThrowsInsufficientFundsException() {
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(newPortfolio.getOption()).thenReturn(option);
        lenient().when(portfoliosRepository.findByOwnerAndOption(eq(person), eq(option))).thenReturn(oldPortfolio);
        when(positionSetter.isThereEnoughMoneyForBuy(newPortfolio)).thenReturn(false);

        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> strategy.addPortfolio(optionDto)),
                () -> verify(producerService, never()).createEvent(any()),
                () -> verify(journalService, never()).addJournal(any(), any())
        );
    }

    @Test
    void whenOptionDtoIsNull_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> strategy.addPortfolio(null));
    }
}