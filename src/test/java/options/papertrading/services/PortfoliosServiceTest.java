package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.facade.interfaces.IPortfolioFacade;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IAdditionStrategyProcessor;
import options.papertrading.services.processors.interfaces.IMarginCallService;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.OptionConverter;
import options.papertrading.util.converters.PortfolioConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfoliosServiceTest {

    @Mock
    private PortfoliosRepository portfoliosRepository;

    @Mock
    private IPersonFacadeHtmlVersion personsService;

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private OptionConverter optionConverter;

    @Mock
    private PortfolioConverter portfolioConverter;

    @Mock
    private IMarginCallService marginCallService;

    @Mock
    private IPortfolioManager portfolioManager;

    @Mock
    private IAdditionStrategyProcessor strategy;

    @InjectMocks
    private PortfoliosService portfoliosService;

    @Test
    void findByOwnerAndOption_ShouldThrowNPE_WhenOwnerIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.findByOwnerAndOption(null, new Option()));
    }

    @Test
    void findByOwnerAndOption_ShouldThrowNPE_WhenOptionIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.findByOwnerAndOption(new Person(), null));
    }

    @Test
    void findAllByOwner_ShouldThrowNPE_WhenOwnerIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.findAllByOwner(null));
    }

    @Test
    void addPortfolioWithId_ShouldThrowNPE_WhenIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.addPortfolio(null, 1, 1));
    }

    @Test
    void addPortfolioWithDto_ShouldThrowNPE_WhenDtoIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.addPortfolio((OptionDto) null));
    }

    @Test
    void addPortfolioIfItIsNotContained_ShouldThrowNPE_WhenDtoIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.addPortfolioIfItIsNotContained(null));
    }

    @Test
    void addPortfolioIfItIsContained_ShouldThrowNPE_WhenDtoIsNull() {
        assertThrows(NullPointerException.class,
                () -> portfoliosService.addPortfolioIfItIsContained(null));
    }

    @Test
    void addPortfolioIfItIsNotContained_ShouldThrowIAE_WhenBuyOrWriteInvalid() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(0); // невалидное значение

        assertThrows(IllegalArgumentException.class,
                () -> portfoliosService.addPortfolioIfItIsNotContained(optionDto));
    }

    @Test
    void findByOwnerAndOption() {
        Person owner = new Person();
        Option option = new Option();
        Portfolio expected = new Portfolio();

        when(portfoliosRepository.findByOwnerAndOption(owner, option)).thenReturn(expected);

        Portfolio result = portfoliosService.findByOwnerAndOption(owner, option);

        assertAll(
                () -> verify(portfoliosRepository).findByOwnerAndOption(owner, option),
                () -> assertEquals(expected, result)
        );
    }

    @Test
    void showAllPortfolios() {
        Person person = new Person();
        List<Portfolio> portfolios = List.of(new Portfolio());
        List<PortfolioDto> expected = List.of(new PortfolioDto());

        when(personsService.getCurrentPerson()).thenReturn(person);
        when(beanFactory.getBean(PortfoliosService.class)).thenReturn(portfoliosService);
        when(portfoliosRepository.findAllByOwner(person)).thenReturn(portfolios);
        when(portfolioConverter.convertListToPortfolioDtoList(portfolios)).thenReturn(expected);

        List<PortfolioDto> result = portfoliosService.showAllPortfolios();

        assertAll(
                () -> verify(portfolioManager).updateCurrentNetPositionAndOpenLimit(),
                () -> verify(portfolioManager).updateAllVariatMargin(),
                () -> verify(marginCallService).marginCallCheck(),
                () -> assertEquals(expected, result)
        );
    }

    @Test
    void findAllByOwner() {
        Person owner = new Person();
        List<Portfolio> expected = List.of(new Portfolio());

        when(portfoliosRepository.findAllByOwner(owner)).thenReturn(expected);

        List<Portfolio> result = portfoliosService.findAllByOwner(owner);

        assertAll(
                () -> verify(portfoliosRepository).findAllByOwner(owner),
                () -> assertEquals(expected, result)
        );
    }

    @Test
    void addPortfolioWithIdAndVolume() {
        String id = "test";
        int volume = 10;
        int buyOrWrite = 1;
        OptionDto optionDto = new OptionDto();

        when(optionConverter.createOptionDtoFromView(id, volume, buyOrWrite)).thenReturn(optionDto);
        IPortfolioFacade portfolioFacade = mock(IPortfolioFacade.class);
        when(beanFactory.getBean(IPortfolioFacade.class)).thenReturn(portfolioFacade);

        portfoliosService.addPortfolio(id, volume, buyOrWrite);

        verify(portfolioFacade).addPortfolio(optionDto);
    }

    @Test
    void addPortfolioWhenContained() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(1);

        Person owner = new Person();
        owner.setUsername("testUser");

        Option option = new Option();
        option.setId("testId");

        Portfolio portfolio = new Portfolio();
        portfolio.setOption(option);
        portfolio.setOwner(owner);

        when(portfolioManager.checkOrderTime(optionDto)).thenReturn(portfolio);
        when(positionSetter.isContained(portfolio)).thenReturn(true);

        PortfoliosService mockService = mock(PortfoliosService.class);
        when(beanFactory.getBean(PortfoliosService.class)).thenReturn(mockService);

        portfoliosService.addPortfolio(optionDto);

        verify(mockService).addPortfolioIfItIsContained(optionDto);
    }

    @Test
    void addPortfolioWhenNotContained() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(1);

        Person owner = new Person();
        owner.setUsername("testUser");

        Option option = new Option();
        option.setId("testId");

        Portfolio portfolio = new Portfolio();
        portfolio.setOption(option);
        portfolio.setOwner(owner);

        when(portfolioManager.checkOrderTime(optionDto)).thenReturn(portfolio);
        when(positionSetter.isContained(portfolio)).thenReturn(false);

        PortfoliosService mockService = mock(PortfoliosService.class);
        when(beanFactory.getBean(PortfoliosService.class)).thenReturn(mockService);

        portfoliosService.addPortfolio(optionDto);

        verify(mockService).addPortfolioIfItIsNotContained(optionDto);
    }

    @Test
    void addPortfolioIfItIsNotContainedBuyCase() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(1);

        portfoliosService.addPortfolioIfItIsNotContained(optionDto);

        verify(strategy).process(AdditionStrategyType.BUYING_IF_NOT_CONTAINED, optionDto);
    }

    @Test
    void addPortfolioIfItIsNotContainedWriteCase() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(-1);

        portfoliosService.addPortfolioIfItIsNotContained(optionDto);

        verify(strategy).process(AdditionStrategyType.WRITING_IF_NOT_CONTAINED, optionDto);
    }

    @Test
    void addPortfolioIfItIsContainedDirectBuyCase() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(1);
        optionDto.setId("testId");

        Person currentPerson = new Person();
        Option option = new Option();
        option.setId("testId");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setOption(option);
        Portfolio oldPortfolio = new Portfolio();

        when(personsService.getCurrentPerson()).thenReturn(currentPerson);
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(optionsRepository.findOneById("testId")).thenReturn(option);
        when(beanFactory.getBean(PortfoliosService.class)).thenReturn(portfoliosService);
        when(portfoliosService.findByOwnerAndOption(currentPerson, option)).thenReturn(oldPortfolio);
        when(positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)).thenReturn(true);

        portfoliosService.addPortfolioIfItIsContained(optionDto);

        verify(strategy).process(AdditionStrategyType.DIRECT_BUYING_IF_CONTAINED, optionDto);
    }

    @Test
    void addPortfolioIfItIsContainedReverseBuyCase() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(1);
        optionDto.setId("testId");

        Person currentPerson = new Person();
        Option option = new Option();
        option.setId("testId");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setOption(option);
        Portfolio oldPortfolio = new Portfolio();

        when(personsService.getCurrentPerson()).thenReturn(currentPerson);
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(optionsRepository.findOneById("testId")).thenReturn(option);
        when(beanFactory.getBean(PortfoliosService.class)).thenReturn(portfoliosService);
        when(portfoliosService.findByOwnerAndOption(currentPerson, option)).thenReturn(oldPortfolio);
        when(positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)).thenReturn(false);

        portfoliosService.addPortfolioIfItIsContained(optionDto);

        verify(strategy).process(AdditionStrategyType.REVERSE_BUYING_IF_CONTAINED, optionDto);
    }

    @Test
    void addPortfolioIfItIsContained_ShouldThrowIAE_WhenBuyOrWriteInvalid() {
        // Подготовка данных
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(0); // невалидное значение
        optionDto.setId("testId");

        Person currentPerson = new Person();
        Option option = new Option();
        option.setId("testId");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setOption(option);

        when(personsService.getCurrentPerson()).thenReturn(currentPerson);
        when(portfolioConverter.convertOptionDtoToPortfolio(optionDto)).thenReturn(newPortfolio);
        when(optionsRepository.findOneById("testId")).thenReturn(option);
        when(beanFactory.getBean(PortfoliosService.class)).thenReturn(portfoliosService);
        when(portfoliosService.findByOwnerAndOption(currentPerson, option)).thenReturn(new Portfolio());

        assertThrows(IllegalArgumentException.class,
                () -> portfoliosService.addPortfolioIfItIsContained(optionDto));
    }
}