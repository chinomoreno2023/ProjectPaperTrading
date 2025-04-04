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
class ReverseWritingPortfolioIfItIsContainedTest {

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
    private ReverseWritingPortfolioIfItIsContained strategy;

    @Test
    void getType_ShouldReturnStrategyType() {
        assertEquals(AdditionStrategyType.REVERSE_WRITING_IF_CONTAINED, strategy.getType());
    }

    @Test
    void whenVolumesEqual_thenClosesPosition() {
        Option mockOption = mock(Option.class);
        Person mockOwner = mock(Person.class);

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(mockOwner);
        when(newPortfolio.getOption()).thenReturn(mockOption);
        when(optionsRepository.findOneById(any())).thenReturn(mockOption);
        when(portfoliosRepository.findByOwnerAndOption(eq(mockOwner), eq(mockOption))).thenReturn(oldPortfolio);
        when(optionDto.getVolume()).thenReturn(5);
        when(newPortfolio.getVolume()).thenReturn(-5);
        when(oldPortfolio.getVolume()).thenReturn(5);
        when(oldPortfolio.getVariatMargin()).thenReturn(100.0);
        when(portfolioManager.updateVariatMargin(oldPortfolio)).thenReturn(50.0);
        when(positionSetter.countPriceInRur(oldPortfolio)).thenReturn(1000.0);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertNull(event.getPortfolioForDelete()),
                () -> assertEquals(oldPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> verify(oldPortfolio).setVolume(0),
                () -> verify(oldPortfolio).setVariatMargin(150.0),
                () -> verify(oldPortfolio).setTradePrice(1000.0)
        );
    }

    @Test
    void whenNewVolumeLess_thenPartiallyClosesPosition() {
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(newPortfolio.getOption()).thenReturn(option);
        when(newPortfolio.getOwner()).thenReturn(person);
        when(optionsRepository.findOneById(any())).thenReturn(option);
        when(portfoliosRepository.findByOwnerAndOption(person, option)).thenReturn(oldPortfolio);
        when(optionDto.getVolume()).thenReturn(3);
        when(newPortfolio.getVolume()).thenReturn(-3);
        when(oldPortfolio.getVolume()).thenReturn(5);
        when(option.getBuyCollateral()).thenReturn(100.0);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        ReverseWritingPortfolioIfItIsContained.class.getMethod("addPortfolio", OptionDto.class),
                        Transactional.class)),
                () -> assertEquals(oldPortfolio, event.getPortfolioForDelete()),
                () -> assertEquals(newPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit()
        );
    }

    @Test
    void whenNewVolumeGreaterAndEnoughMoney_thenClosesAndOpensNew() {
        Person mockOwner = mock(Person.class);
        Option mockOption = mock(Option.class);

        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(mockOwner);
        when(optionsRepository.findOneById(any())).thenReturn(mockOption);
        when(portfoliosRepository.findByOwnerAndOption(mockOwner, mockOption)).thenReturn(oldPortfolio);

        when(optionDto.getVolume()).thenReturn(7);
        when(newPortfolio.getVolume()).thenReturn(-7);
        when(oldPortfolio.getVolume()).thenReturn(5);

        when(newPortfolio.getOption()).thenReturn(mockOption);
        when(oldPortfolio.getOption()).thenReturn(mockOption);
        when(mockOption.getBuyCollateral()).thenReturn(100.0);
        when(mockOption.getWriteCollateral()).thenReturn(150.0);

        when(positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio)).thenReturn(true);

        when(newPortfolio.getOwner()).thenReturn(mockOwner);

        lenient().when(mockOwner.getCurrentNetPosition()).thenReturn(1000.0);
        lenient().when(mockOwner.getOpenLimit()).thenReturn(5000.0);

        strategy.addPortfolio(optionDto);

        verify(producerService).createEvent(eventCaptor.capture());
        TradeCreatedEventDto event = eventCaptor.getValue();

        assertAll(
                () -> assertEquals(oldPortfolio, event.getPortfolioForDelete()),
                () -> assertEquals(newPortfolio, event.getPortfolioForSave()),
                () -> verify(journalService).addJournal(newPortfolio, optionDto),
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> verify(mockOwner).setCurrentNetPosition(1000.0 - 100.0 * 5 + 150.0 * 7),
                () -> verify(mockOwner).setOpenLimit(5000.0 + 100.0 * 5 - 150.0 * 7),
                () -> verify(newPortfolio).setVolume(-7 + 5),
                () -> verify(newPortfolio).setTradePrice(anyDouble()),
                () -> verify(newPortfolio).setVolatilityWhenWasTrade(anyDouble()),
                () -> verify(newPortfolio).setCollateralWhenWasTrade(150.0)
        );
    }

    @Test
    void whenNewVolumeGreaterAndNotEnoughMoney_thenThrowsException() {
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(personsService.getCurrentPerson()).thenReturn(person);
        lenient().when(newPortfolio.getOption()).thenReturn(option);
        lenient().when(portfoliosRepository.findByOwnerAndOption(any(), any())).thenReturn(oldPortfolio);
        when(optionDto.getVolume()).thenReturn(7);
        when(newPortfolio.getVolume()).thenReturn(-7);
        when(oldPortfolio.getVolume()).thenReturn(5);
        when(positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio)).thenReturn(false);

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