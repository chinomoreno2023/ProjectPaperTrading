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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReverseBuyingPortfolioIfItIsContainedTest {

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
    private BeanFactory beanFactory;

    @Mock
    private Portfolio newPortfolio;

    @Mock
    private Portfolio oldPortfolio;

    @Mock
    private OptionDto optionDto;

    @Mock
    private Person person;

    @Mock
    private OptionDto convertedOptionDto;

    @Captor
    private
    ArgumentCaptor<TradeCreatedEventDto> eventCaptor;

    @InjectMocks
    private ReverseBuyingPortfolioIfItIsContained strategy;

    @Test
    void getType_ShouldReturnStrategyType() {
        assertEquals(AdditionStrategyType.REVERSE_BUYING_IF_CONTAINED, strategy.getType());
    }

    @Test
    void whenVolumesAreEqual_thenPositionIsClosed() {
        Option mockOption = mock(Option.class);
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(person);

        when(newPortfolio.getOption()).thenReturn(mockOption);

        when(optionsRepository.findOneById(any())).thenReturn(mockOption);

        when(portfoliosRepository.findByOwnerAndOption(person, mockOption)).thenReturn(oldPortfolio);

        when(newPortfolio.getVolume()).thenReturn(10);
        when(oldPortfolio.getVolume()).thenReturn(-10);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        ReverseBuyingPortfolioIfItIsContained.class.getMethod("addPortfolio", OptionDto.class),
                        Transactional.class)),
                () -> assertNull(event.getPortfolioForDelete()),
                () -> assertEquals(oldPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> verify(oldPortfolio).setVolume(0)
        );
    }

    @Test
    void whenNewVolumeLess_thenPartiallyClosesPosition() {
        Option mockOption = mock(Option.class);
        Portfolio oldPortfolio = mock(Portfolio.class);
        OptionDto convertedOptionDto = mock(OptionDto.class); // Мок для конвертированного OptionDto

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(portfolioConverter.convertPortfolioToOptionDto(newPortfolio)).thenReturn(convertedOptionDto);
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(newPortfolio.getOption()).thenReturn(mockOption);
        when(newPortfolio.getOwner()).thenReturn(person);
        when(optionsRepository.findOneById(any())).thenReturn(mockOption);
        when(portfoliosRepository.findByOwnerAndOption(eq(person), eq(mockOption))).thenReturn(oldPortfolio);
        when(newPortfolio.getVolume()).thenReturn(5);
        when(oldPortfolio.getVolume()).thenReturn(-10);
        when(beanFactory.getBean(ReverseBuyingPortfolioIfItIsContained.class)).thenReturn(strategy);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(oldPortfolio, event.getPortfolioForDelete()),
                () -> assertEquals(newPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> verify(convertedOptionDto).setBuyOrWrite(1)
        );
    }

    @Test
    void whenNewVolumeGreaterAndEnoughMoney_thenClosesAndOpensNew() {
        Option mockNewOption = mock(Option.class);
        Option mockOldOption = mock(Option.class);
        Portfolio oldPortfolio = mock(Portfolio.class);
        OptionDto convertedOptionDto = mock(OptionDto.class);

        when(newPortfolio.getOption()).thenReturn(mockNewOption);
        when(oldPortfolio.getOption()).thenReturn(mockOldOption);

        when(mockOldOption.getWriteCollateral()).thenReturn(100.0);
        when(mockNewOption.getBuyCollateral()).thenReturn(150.0);

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(portfolioConverter.convertPortfolioToOptionDto(newPortfolio)).thenReturn(convertedOptionDto);
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(newPortfolio.getOwner()).thenReturn(person);
        when(optionsRepository.findOneById(any())).thenReturn(mockNewOption);
        when(portfoliosRepository.findByOwnerAndOption(eq(person), eq(mockNewOption))).thenReturn(oldPortfolio);
        when(newPortfolio.getVolume()).thenReturn(15);
        when(oldPortfolio.getVolume()).thenReturn(-10);
        when(positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio)).thenReturn(true);
        when(beanFactory.getBean(ReverseBuyingPortfolioIfItIsContained.class)).thenReturn(strategy);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(oldPortfolio, event.getPortfolioForDelete()),
                () -> assertEquals(newPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> verify(convertedOptionDto).setBuyOrWrite(1),
                () -> verify(newPortfolio.getOwner()).setCurrentNetPosition(anyDouble()),
                () -> verify(newPortfolio.getOwner()).setOpenLimit(anyDouble())
        );
    }

    @Test
    void whenNewVolumeGreaterAndNotEnoughMoney_thenThrowsException() {
        Portfolio oldPortfolio = mock(Portfolio.class);
        Option mockOption = mock(Option.class);

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(person);
        lenient().when(newPortfolio.getOption()).thenReturn(mockOption);
        lenient().when(newPortfolio.getOwner()).thenReturn(person);
        lenient().when(optionsRepository.findOneById(any())).thenReturn(mockOption);
        lenient().when(portfoliosRepository.findByOwnerAndOption(any(), any())).thenReturn(oldPortfolio);
        lenient().when(newPortfolio.getVolume()).thenReturn(15);
        lenient().when(oldPortfolio.getVolume()).thenReturn(-10);

        when(positionSetter.isThereEnoughMoneyForReverseBuy(any(), any())).thenReturn(false);

        assertAll(
                () -> assertThrows(InsufficientFundsException.class, () -> strategy.addPortfolio(optionDto)),
                () -> verify(producerService, never()).createEvent(any()),
                () -> verify(journalService, never()).addJournal(any(), any())
        );
    }

    @Test
    void whenOptionDtoIsNull_thenThrowsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> strategy.addPortfolio(null));
    }

    @Test
    void shouldPerformFinalOperationsCorrectly() {
        when(portfolioConverter.convertPortfolioToOptionDto(newPortfolio)).thenReturn(convertedOptionDto);
        when(oldPortfolio.getVolume()).thenReturn(-5);
        when(oldPortfolio.getVariatMargin()).thenReturn(100.0);
        when(portfolioManager.updateVariatMargin(oldPortfolio)).thenReturn(50.0);
        when(newPortfolio.getVolume()).thenReturn(10);
        when(newPortfolio.getOption()).thenReturn(mock(Option.class));

        strategy.doFinalOperations(newPortfolio, oldPortfolio);

        assertAll(
                () -> verify(convertedOptionDto).setBuyOrWrite(1),
                () -> verify(newPortfolio).setVariatMargin(50.0), // 100 + (-1 * 50)
                () -> verify(newPortfolio).setVolume(5), // 10 - abs(-5)
                () -> verify(newPortfolio).setTradePrice(anyDouble()),
                () -> verify(newPortfolio).setVolatilityWhenWasTrade(anyDouble())
        );
    }

    @Test
    void whenOldPortfolioVolumeIsZero_thenHandlesCorrectly() {
        Option mockOption = mock(Option.class);
        double expectedCalculatedTradePrice = 12345.67;
        double mockVolatility = 0.35;

        when(portfolioConverter.convertPortfolioToOptionDto(newPortfolio)).thenReturn(convertedOptionDto);
        when(oldPortfolio.getVolume()).thenReturn(0);
        when(oldPortfolio.getVariatMargin()).thenReturn(100.0);
        when(newPortfolio.getVolume()).thenReturn(10);
        when(newPortfolio.getOption()).thenReturn(mockOption);
        when(mockOption.getVolatility()).thenReturn(mockVolatility);
        when(portfolioManager.updateVariatMargin(oldPortfolio)).thenReturn(50.0);
        when(positionSetter.countPriceInRur(newPortfolio)).thenReturn(expectedCalculatedTradePrice);

        strategy.doFinalOperations(newPortfolio, oldPortfolio);

        assertAll(
                () -> verify(convertedOptionDto).setBuyOrWrite(1),
                () -> verify(newPortfolio).setVariatMargin(50.0),
                () -> verify(newPortfolio).setVolume(10),
                () -> verify(newPortfolio).setTradePrice(expectedCalculatedTradePrice),
                () -> verify(newPortfolio).setVolatilityWhenWasTrade(mockVolatility)
        );
    }

    @Test
    void whenNewPortfolioIsNull_thenThrowsException() {
        assertThrows(NullPointerException.class,
                () -> strategy.doFinalOperations(null, oldPortfolio));
    }

    @Test
    void whenOldPortfolioIsNull_thenThrowsException() {
        assertThrows(NullPointerException.class,
                () -> strategy.doFinalOperations(newPortfolio, null));
    }
}
