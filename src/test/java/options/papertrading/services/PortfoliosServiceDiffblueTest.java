package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.exceptions.InsufficientFundsException;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.mappers.PortfolioMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PortfoliosService.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PortfoliosServiceDiffblueTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private JournalRepository journalRepository;

    @MockBean
    private JournalService journalService;

    @MockBean
    private OptionsService optionsService;

    @MockBean
    private PersonsService personsService;

    @MockBean
    private PortfolioMapper portfolioMapper;

    @MockBean
    private PortfoliosRepository portfoliosRepository;

    @MockBean
    private PositionSetter positionSetter;

    @MockBean
    private SmtpMailSender smtpMailSender;

    @MockBean
    private TextConverter textConverter;

    @Autowired
    private PortfoliosService portfoliosService;

    /**
     * Method under test:
     * {@link PortfoliosService#findByOwnerAndOption(Person, Option)}
     */
    @Test
    public void testFindByOwnerAndOption() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.findByOwnerAndOption(Mockito.<Person>any(), Mockito.<Option>any())).thenReturn(portfolio);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        // Act
        Portfolio actualFindByOwnerAndOptionResult = portfoliosService.findByOwnerAndOption(owner2, option2);

        // Assert
        verify(portfoliosRepository).findByOwnerAndOption(isA(Person.class), isA(Option.class));
        assertSame(portfolio, actualFindByOwnerAndOptionResult);
    }

    /**
     * Method under test: {@link PortfoliosService#convertToPortfolioDto(Portfolio)}
     */
    @Test
    public void testConvertToPortfolioDto() {
        // Arrange
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setId("42");
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        when(portfolioMapper.convertToPortfolioDto(Mockito.<Portfolio>any())).thenReturn(portfolioDto);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);

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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        // Act
        PortfolioDto actualConvertToPortfolioDtoResult = portfoliosService.convertToPortfolioDto(portfolio);

        // Assert
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(portfolioMapper).convertToPortfolioDto(isA(Portfolio.class));
        assertSame(portfolioDto, actualConvertToPortfolioDtoResult);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#convertListToPortfolioDtoList(List)}
     */
    @Test
    public void testConvertListToPortfolioDtoList() {
        // Arrange, Act and Assert
        assertTrue(portfoliosService.convertListToPortfolioDtoList(new ArrayList<>()).isEmpty());
    }

    /**
     * Method under test:
     * {@link PortfoliosService#convertListToPortfolioDtoList(List)}
     */
    @Test
    public void testConvertListToPortfolioDtoList2() {
        // Arrange
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setId("42");
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        when(portfolioMapper.convertToPortfolioDto(Mockito.<Portfolio>any())).thenReturn(portfolioDto);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);

        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        Person owner = new Person();
        owner.setActivationCode("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        owner.setUsername("janedoe");

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        ArrayList<Portfolio> portfolios = new ArrayList<>();
        portfolios.add(portfolio);

        // Act
        List<PortfolioDto> actualConvertListToPortfolioDtoListResult = portfoliosService
                .convertListToPortfolioDtoList(portfolios);

        // Assert
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(portfolioMapper).convertToPortfolioDto(isA(Portfolio.class));
        assertEquals(1, actualConvertListToPortfolioDtoListResult.size());
        assertSame(portfolioDto, actualConvertListToPortfolioDtoListResult.get(0));
    }

    /**
     * Method under test:
     * {@link PortfoliosService#convertListToPortfolioDtoList(List)}
     */
    @Test
    public void testConvertListToPortfolioDtoList3() {
        // Arrange
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setId("42");
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        when(portfolioMapper.convertToPortfolioDto(Mockito.<Portfolio>any())).thenReturn(portfolioDto);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);

        Option option = new Option();
        option.setBuyCollateral(10.0d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(10.0d);
        option.setStepPrice(10.0d);
        option.setStrike(1);
        option.setType("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        option.setVolatility(10.0d);
        option.setWriteCollateral(10.0d);

        Person owner = new Person();
        owner.setActivationCode("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        owner.setUsername("janedoe");

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(0.5d);
        option2.setDaysToMaturity(0);
        option2.setId("Converting {} to portfolioDto. Result: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(0.5d);
        option2.setStepPrice(0.5d);
        option2.setStrike(0);
        option2.setType("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        option2.setVolatility(0.5d);
        option2.setWriteCollateral(0.5d);

        Person owner2 = new Person();
        owner2.setActivationCode("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(0.5d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(0.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Converting {} to portfolioDto. Result: {}");
        owner2.setRole("Converting portfolio list '{}' to portfolioDto list. Result: {} ");
        owner2.setUsername("Converting {} to portfolioDto. Result: {}");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setCollateralWhenWasTrade(0.5d);
        portfolio2.setId(2L);
        portfolio2.setOption(option2);
        portfolio2.setOwner(owner2);
        portfolio2.setTradePrice(0.5d);
        portfolio2.setVariatMargin(0.5d);
        portfolio2.setVolatilityWhenWasTrade(0.5d);
        portfolio2.setVolume(0);

        ArrayList<Portfolio> portfolios = new ArrayList<>();
        portfolios.add(portfolio2);
        portfolios.add(portfolio);

        // Act
        List<PortfolioDto> actualConvertListToPortfolioDtoListResult = portfoliosService
                .convertListToPortfolioDtoList(portfolios);

        // Assert
        verify(positionSetter, atLeast(1)).countPriceInRur(Mockito.<Portfolio>any());
        verify(portfolioMapper, atLeast(1)).convertToPortfolioDto(Mockito.<Portfolio>any());
        assertEquals(2, actualConvertListToPortfolioDtoListResult.size());
        assertSame(portfolioDto, actualConvertListToPortfolioDtoListResult.get(0));
        assertSame(portfolioDto, actualConvertListToPortfolioDtoListResult.get(1));
    }

    /**
     * Method under test: {@link PortfoliosService#showAllPortfolios()}
     */
    @Test
    public void testShowAllPortfolios() {
        // Arrange
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(new ArrayList<>());

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
        when(personsService.getCurrentPerson()).thenReturn(person);
        doNothing().when(positionSetter).updateAllVariatMargin();
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();

        // Act
        List<PortfolioDto> actualShowAllPortfoliosResult = portfoliosService.showAllPortfolios();

        // Assert
        verify(portfoliosRepository, atLeast(1)).findAllByOwner(isA(Person.class));
        verify(personsService, atLeast(1)).getCurrentPerson();
        verify(positionSetter).updateAllVariatMargin();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        assertTrue(actualShowAllPortfoliosResult.isEmpty());
    }

    /**
     * Method under test: {@link PortfoliosService#showAllPortfolios()}
     */
    @Test
    public void testShowAllPortfolios2() {
        // Arrange
        doThrow(new RuntimeException("Owner: {}, options in portfolio: {}")).when(positionSetter)
                .updateCurrentNetPositionAndOpenLimit();

        // Act and Assert
        thrown.expect(RuntimeException.class);
        portfoliosService.showAllPortfolios();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
    }

    /**
     * Method under test: {@link PortfoliosService#showAllPortfolios()}
     */
    @Test
    public void testShowAllPortfolios3() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(0.5d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(0.5d);
        option.setStepPrice(0.5d);
        option.setStrike(1);
        option.setType("Owner: {}, options in portfolio: {}");
        option.setVolatility(0.5d);
        option.setWriteCollateral(0.5d);

        Person owner = new Person();
        owner.setActivationCode("Owner: {}, options in portfolio: {}");
        owner.setActive(true);
        owner.setCurrentNetPosition(0.5d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(0.5d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Owner: {}, options in portfolio: {}");
        owner.setUsername("janedoe");

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(0.5d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(0.5d);
        portfolio.setVariatMargin(0.5d);
        portfolio.setVolatilityWhenWasTrade(0.5d);
        portfolio.setVolume(1);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio);
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(portfolioList);

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setId("42");
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        when(portfolioMapper.convertToPortfolioDto(Mockito.<Portfolio>any())).thenReturn(portfolioDto);

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
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(positionSetter).updateAllVariatMargin();
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();

        // Act
        List<PortfolioDto> actualShowAllPortfoliosResult = portfoliosService.showAllPortfolios();

        // Assert
        verify(portfoliosRepository, atLeast(1)).findAllByOwner(isA(Person.class));
        verify(personsService, atLeast(1)).getCurrentPerson();
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).updateAllVariatMargin();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(portfolioMapper).convertToPortfolioDto(isA(Portfolio.class));
        assertEquals(1, actualShowAllPortfoliosResult.size());
        assertSame(portfolioDto, actualShowAllPortfoliosResult.get(0));
    }

    /**
     * Method under test: {@link PortfoliosService#showAllPortfolios()}
     */
    @Test
    public void testShowAllPortfolios4() {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(0.5d);
        option.setDaysToMaturity(1);
        option.setId("42");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(0.5d);
        option.setStepPrice(0.5d);
        option.setStrike(1);
        option.setType("Owner: {}, options in portfolio: {}");
        option.setVolatility(0.5d);
        option.setWriteCollateral(0.5d);

        Person owner = new Person();
        owner.setActivationCode("Owner: {}, options in portfolio: {}");
        owner.setActive(true);
        owner.setCurrentNetPosition(0.5d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(0.5d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Owner: {}, options in portfolio: {}");
        owner.setUsername("janedoe");

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(0.5d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(0.5d);
        portfolio.setVariatMargin(0.5d);
        portfolio.setVolatilityWhenWasTrade(0.5d);
        portfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(5.0d);
        option2.setDaysToMaturity(0);
        option2.setId("Owner: {}, options in portfolio: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(5.0d);
        option2.setStepPrice(5.0d);
        option2.setStrike(0);
        option2.setType("Converting {} to portfolioDto. Result: {}");
        option2.setVolatility(5.0d);
        option2.setWriteCollateral(5.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Converting {} to portfolioDto. Result: {}");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(5.0d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(5.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Owner: {}, options in portfolio: {}");
        owner2.setRole("Converting {} to portfolioDto. Result: {}");
        owner2.setUsername("Owner: {}, options in portfolio: {}");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setCollateralWhenWasTrade(5.0d);
        portfolio2.setId(2L);
        portfolio2.setOption(option2);
        portfolio2.setOwner(owner2);
        portfolio2.setTradePrice(5.0d);
        portfolio2.setVariatMargin(5.0d);
        portfolio2.setVolatilityWhenWasTrade(5.0d);
        portfolio2.setVolume(0);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio2);
        portfolioList.add(portfolio);
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(portfolioList);

        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setBuyCollateral(10.0d);
        portfolioDto.setCollateralWhenWasTrade(10.0d);
        portfolioDto.setCurrentNetPosition(10.0d);
        portfolioDto.setDaysToMaturity(1);
        portfolioDto.setId("42");
        portfolioDto.setOpenLimit(10.0d);
        portfolioDto.setPrice(10.0d);
        portfolioDto.setStepPrice(10.0d);
        portfolioDto.setStrike(1);
        portfolioDto.setTradePrice(10.0d);
        portfolioDto.setType("Type");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);
        when(portfolioMapper.convertToPortfolioDto(Mockito.<Portfolio>any())).thenReturn(portfolioDto);

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
        when(personsService.getCurrentPerson()).thenReturn(person);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(positionSetter).updateAllVariatMargin();
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();

        // Act
        List<PortfolioDto> actualShowAllPortfoliosResult = portfoliosService.showAllPortfolios();

        // Assert
        verify(portfoliosRepository, atLeast(1)).findAllByOwner(isA(Person.class));
        verify(personsService, atLeast(1)).getCurrentPerson();
        verify(positionSetter, atLeast(1)).countPriceInRur(Mockito.<Portfolio>any());
        verify(positionSetter).updateAllVariatMargin();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(portfolioMapper, atLeast(1)).convertToPortfolioDto(Mockito.<Portfolio>any());
        assertEquals(2, actualShowAllPortfoliosResult.size());
        assertSame(portfolioDto, actualShowAllPortfoliosResult.get(0));
        assertSame(portfolioDto, actualShowAllPortfoliosResult.get(1));
    }

    /**
     * Method under test: {@link PortfoliosService#findAllByOwner(Person)}
     */
    @Test
    public void testFindAllByOwner() {
        // Arrange
        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(portfolioList);

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

        // Act
        List<Portfolio> actualFindAllByOwnerResult = portfoliosService.findAllByOwner(owner);

        // Assert
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        assertTrue(actualFindAllByOwnerResult.isEmpty());
        assertEquals(actualFindAllByOwnerResult, owner.getJournal());
        assertEquals(actualFindAllByOwnerResult, owner.getOptionsInPortfolio());
        assertSame(portfolioList, actualFindAllByOwnerResult);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#convertOptionDtoToPortfolio(OptionDto)}
     */
    @Test
    public void testConvertOptionDtoToPortfolio() {
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
        when(personsService.getCurrentPerson()).thenReturn(person);

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
        when(optionsService.findByOptionId(Mockito.<String>any())).thenReturn(option);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);

        // Act
        Portfolio actualConvertOptionDtoToPortfolioResult = portfoliosService.convertOptionDtoToPortfolio(optionDto);

        // Assert
        verify(optionsService).findByOptionId(eq("42"));
        verify(personsService).getCurrentPerson();
        assertEquals(0.0d, actualConvertOptionDtoToPortfolioResult.getCollateralWhenWasTrade(), 0.0);
        assertEquals(0.0d, actualConvertOptionDtoToPortfolioResult.getTradePrice(), 0.0);
        assertEquals(0.0d, actualConvertOptionDtoToPortfolioResult.getVariatMargin(), 0.0);
        assertEquals(0.0d, actualConvertOptionDtoToPortfolioResult.getVolatilityWhenWasTrade(), 0.0);
        assertEquals(0L, actualConvertOptionDtoToPortfolioResult.getId());
        assertEquals(1, actualConvertOptionDtoToPortfolioResult.getVolume());
        assertSame(option, actualConvertOptionDtoToPortfolioResult.getOption());
        assertSame(person, actualConvertOptionDtoToPortfolioResult.getOwner());
    }

    /**
     * Method under test:
     * {@link PortfoliosService#convertPortfolioToOptionDto(Portfolio)}
     */
    @Test
    public void testConvertPortfolioToOptionDto() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        // Act
        OptionDto actualConvertPortfolioToOptionDtoResult = portfoliosService.convertPortfolioToOptionDto(portfolio);

        // Assert
        assertEquals("42", actualConvertPortfolioToOptionDtoResult.getId());
        assertEquals("Type", actualConvertPortfolioToOptionDtoResult.getType());
        assertEquals(0, actualConvertPortfolioToOptionDtoResult.getBuyOrWrite());
        assertEquals(0.0d, actualConvertPortfolioToOptionDtoResult.getStepPrice(), 0.0);
        assertEquals(1, actualConvertPortfolioToOptionDtoResult.getDaysToMaturity());
        assertEquals(1, actualConvertPortfolioToOptionDtoResult.getStrike());
        assertEquals(1, actualConvertPortfolioToOptionDtoResult.getVolume());
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getBuyCollateral(), 0.0);
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getPrice(), 0.0);
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getVolatility(), 0.0);
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getWriteCollateral(), 0.0);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#convertPortfolioToOptionDto(Portfolio)}
     */
    @Test
    public void testConvertPortfolioToOptionDto2() {
        // Arrange
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

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);
        Portfolio portfolio = mock(Portfolio.class);
        when(portfolio.getVolume()).thenReturn(1);
        when(portfolio.getOption()).thenReturn(option2);
        doNothing().when(portfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(portfolio).setId(anyLong());
        doNothing().when(portfolio).setOption(Mockito.<Option>any());
        doNothing().when(portfolio).setOwner(Mockito.<Person>any());
        doNothing().when(portfolio).setTradePrice(anyDouble());
        doNothing().when(portfolio).setVariatMargin(anyDouble());
        doNothing().when(portfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(portfolio).setVolume(anyInt());
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        // Act
        OptionDto actualConvertPortfolioToOptionDtoResult = portfoliosService.convertPortfolioToOptionDto(portfolio);

        // Assert
        verify(portfolio, atLeast(1)).getOption();
        verify(portfolio).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio).setVariatMargin(eq(10.0d));
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertEquals("42", actualConvertPortfolioToOptionDtoResult.getId());
        assertEquals("Type", actualConvertPortfolioToOptionDtoResult.getType());
        assertEquals(0, actualConvertPortfolioToOptionDtoResult.getBuyOrWrite());
        assertEquals(0.0d, actualConvertPortfolioToOptionDtoResult.getStepPrice(), 0.0);
        assertEquals(1, actualConvertPortfolioToOptionDtoResult.getDaysToMaturity());
        assertEquals(1, actualConvertPortfolioToOptionDtoResult.getStrike());
        assertEquals(1, actualConvertPortfolioToOptionDtoResult.getVolume());
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getBuyCollateral(), 0.0);
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getPrice(), 0.0);
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getVolatility(), 0.0);
        assertEquals(10.0d, actualConvertPortfolioToOptionDtoResult.getWriteCollateral(), 0.0);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#addPortfolioIfItIsNotContained(OptionDto)}
     */
    @Test
    public void testAddPortfolioIfItIsNotContained() {
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
        when(personsService.getCurrentPerson()).thenReturn(person);

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
        when(optionsService.findByOptionId(Mockito.<String>any())).thenReturn(option);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);

        // Act
        portfoliosService.addPortfolioIfItIsNotContained(optionDto);

        // Assert
        verify(optionsService).findByOptionId(eq("42"));
        verify(personsService).getCurrentPerson();
    }

    /**
     * Method under test:
     * {@link PortfoliosService#addPortfolioIfItIsNotContained(OptionDto)}
     */
    @Test
    public void testAddPortfolioIfItIsNotContained2() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.save(Mockito.<Portfolio>any())).thenReturn(portfolio);

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
        when(personsService.getCurrentPerson()).thenReturn(person);

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);
        when(optionsService.findByOptionId(Mockito.<String>any())).thenReturn(option2);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        when(positionSetter.isThereEnoughMoneyForWrite(Mockito.<Portfolio>any())).thenReturn(true);
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(-1);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);

        // Act
        portfoliosService.addPortfolioIfItIsNotContained(optionDto);

        // Assert
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(optionsService).findByOptionId(eq("42"));
        verify(personsService).getCurrentPerson();
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).isThereEnoughMoneyForWrite(isA(Portfolio.class));
        verify(portfoliosRepository).save(isA(Portfolio.class));
    }

    /**
     * Method under test:
     * {@link PortfoliosService#addPortfolioIfItIsContained(OptionDto)}
     */
    @Test
    public void testAddPortfolioIfItIsContained() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.findByOwnerAndOption(Mockito.<Person>any(), Mockito.<Option>any())).thenReturn(portfolio);

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
        when(personsService.getCurrentPerson()).thenReturn(person);

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);
        when(optionsService.findByOptionId(Mockito.<String>any())).thenReturn(option2);

        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("Type");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);

        // Act
        portfoliosService.addPortfolioIfItIsContained(optionDto);

        // Assert
        verify(portfoliosRepository).findByOwnerAndOption(isA(Person.class), isA(Option.class));
        verify(optionsService, atLeast(1)).findByOptionId(eq("42"));
        verify(personsService, atLeast(1)).getCurrentPerson();
    }

    /**
     * Method under test:
     * {@link PortfoliosService#directBuyPortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testDirectBuyPortfolioIfItIsContained() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.save(Mockito.<Portfolio>any())).thenReturn(portfolio);
        doNothing().when(portfoliosRepository).delete(Mockito.<Portfolio>any());
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        when(positionSetter.updateVariatMargin(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        when(positionSetter.isThereEnoughMoneyForBuy(Mockito.<Portfolio>any())).thenReturn(true);
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option2);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option3 = new Option();
        option3.setBuyCollateral(10.0d);
        option3.setDaysToMaturity(1);
        option3.setId("42");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(10.0d);
        option3.setStepPrice(10.0d);
        option3.setStrike(1);
        option3.setType("Type");
        option3.setVolatility(10.0d);
        option3.setWriteCollateral(10.0d);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(10.0d);
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(10.0d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("iloveyou");
        owner3.setRole("Role");
        owner3.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option3);
        oldPortfolio.setOwner(owner3);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act
        portfoliosService.directBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);

        // Assert
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).isThereEnoughMoneyForBuy(isA(Portfolio.class));
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(positionSetter).updateVariatMargin(isA(Portfolio.class));
        verify(portfoliosRepository).delete(isA(Portfolio.class));
        verify(portfoliosRepository).save(isA(Portfolio.class));
        Person owner4 = newPortfolio.getOwner();
        assertEquals(0.0d, owner4.getOpenLimit(), 0.0);
        assertEquals(2, newPortfolio.getVolume());
        assertEquals(20.0d, owner4.getCurrentNetPosition(), 0.0);
        assertEquals(20.0d, newPortfolio.getVariatMargin(), 0.0);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#directBuyPortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testDirectBuyPortfolioIfItIsContained2() {
        // Arrange
        when(positionSetter.isThereEnoughMoneyForBuy(Mockito.<Portfolio>any())).thenReturn(false);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option);
        newPortfolio.setOwner(owner);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option2);
        oldPortfolio.setOwner(owner2);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act and Assert
        thrown.expect(InsufficientFundsException.class);
        portfoliosService.directBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
        verify(positionSetter).isThereEnoughMoneyForBuy(isA(Portfolio.class));
    }

    /**
     * Method under test:
     * {@link PortfoliosService#reverseBuyPortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testReverseBuyPortfolioIfItIsContained() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.save(Mockito.<Portfolio>any())).thenReturn(portfolio);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        when(positionSetter.updateVariatMargin(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option2);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option3 = new Option();
        option3.setBuyCollateral(10.0d);
        option3.setDaysToMaturity(1);
        option3.setId("42");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(10.0d);
        option3.setStepPrice(10.0d);
        option3.setStrike(1);
        option3.setType("Type");
        option3.setVolatility(10.0d);
        option3.setWriteCollateral(10.0d);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(10.0d);
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(10.0d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("iloveyou");
        owner3.setRole("Role");
        owner3.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option3);
        oldPortfolio.setOwner(owner3);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act
        portfoliosService.reverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);

        // Assert
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(positionSetter).updateVariatMargin(isA(Portfolio.class));
        verify(portfoliosRepository).save(isA(Portfolio.class));
    }

    /**
     * Method under test:
     * {@link PortfoliosService#finalOperationsForReverseBuyPortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testFinalOperationsForReverseBuyPortfolioIfItIsContained() {
        // Arrange
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        when(positionSetter.updateVariatMargin(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option);
        newPortfolio.setOwner(owner);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option2);
        oldPortfolio.setOwner(owner2);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act
        portfoliosService.finalOperationsForReverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);

        // Assert
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).updateVariatMargin(isA(Portfolio.class));
        assertEquals(0, newPortfolio.getVolume());
        assertEquals(0.0d, newPortfolio.getVariatMargin(), 0.0);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#directWritePortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testDirectWritePortfolioIfItIsContained() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.save(Mockito.<Portfolio>any())).thenReturn(portfolio);
        doNothing().when(portfoliosRepository).delete(Mockito.<Portfolio>any());
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        when(positionSetter.updateVariatMargin(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        when(positionSetter.isThereEnoughMoneyForWrite(Mockito.<Portfolio>any())).thenReturn(true);
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option2);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option3 = new Option();
        option3.setBuyCollateral(10.0d);
        option3.setDaysToMaturity(1);
        option3.setId("42");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(10.0d);
        option3.setStepPrice(10.0d);
        option3.setStrike(1);
        option3.setType("Type");
        option3.setVolatility(10.0d);
        option3.setWriteCollateral(10.0d);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(10.0d);
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(10.0d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("iloveyou");
        owner3.setRole("Role");
        owner3.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option3);
        oldPortfolio.setOwner(owner3);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act
        portfoliosService.directWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);

        // Assert
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).isThereEnoughMoneyForWrite(isA(Portfolio.class));
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(positionSetter).updateVariatMargin(isA(Portfolio.class));
        verify(portfoliosRepository).delete(isA(Portfolio.class));
        verify(portfoliosRepository).save(isA(Portfolio.class));
        assertEquals(-2, newPortfolio.getVolume());
        Person owner4 = newPortfolio.getOwner();
        assertEquals(0.0d, owner4.getOpenLimit(), 0.0);
        assertEquals(0.0d, newPortfolio.getVariatMargin(), 0.0);
        assertEquals(20.0d, owner4.getCurrentNetPosition(), 0.0);
    }

    /**
     * Method under test:
     * {@link PortfoliosService#directWritePortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testDirectWritePortfolioIfItIsContained2() {
        // Arrange
        when(positionSetter.isThereEnoughMoneyForWrite(Mockito.<Portfolio>any())).thenReturn(false);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option);
        newPortfolio.setOwner(owner);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option2);
        oldPortfolio.setOwner(owner2);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act and Assert
        thrown.expect(InsufficientFundsException.class);
        portfoliosService.directWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
        verify(positionSetter).isThereEnoughMoneyForWrite(isA(Portfolio.class));
    }

    /**
     * Method under test:
     * {@link PortfoliosService#directWritePortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testDirectWritePortfolioIfItIsContained3() {
        // Arrange
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        when(positionSetter.isThereEnoughMoneyForWrite(Mockito.<Portfolio>any())).thenReturn(true);

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

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);
        Portfolio newPortfolio = mock(Portfolio.class);
        when(newPortfolio.getVolume()).thenThrow(new RuntimeException("Direct writing"));
        when(newPortfolio.getOption()).thenReturn(option2);
        doNothing().when(newPortfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(newPortfolio).setId(anyLong());
        doNothing().when(newPortfolio).setOption(Mockito.<Option>any());
        doNothing().when(newPortfolio).setOwner(Mockito.<Person>any());
        doNothing().when(newPortfolio).setTradePrice(anyDouble());
        doNothing().when(newPortfolio).setVariatMargin(anyDouble());
        doNothing().when(newPortfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(newPortfolio).setVolume(anyInt());
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option);
        newPortfolio.setOwner(owner);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option3 = new Option();
        option3.setBuyCollateral(10.0d);
        option3.setDaysToMaturity(1);
        option3.setId("42");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(10.0d);
        option3.setStepPrice(10.0d);
        option3.setStrike(1);
        option3.setType("Type");
        option3.setVolatility(10.0d);
        option3.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option3);
        oldPortfolio.setOwner(owner2);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act and Assert
        thrown.expect(RuntimeException.class);
        portfoliosService.directWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
        verify(newPortfolio, atLeast(1)).getOption();
        verify(newPortfolio).getVolume();
        verify(newPortfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(newPortfolio).setId(eq(1L));
        verify(newPortfolio).setOption(isA(Option.class));
        verify(newPortfolio).setOwner(isA(Person.class));
        verify(newPortfolio).setTradePrice(eq(10.0d));
        verify(newPortfolio).setVariatMargin(eq(10.0d));
        verify(newPortfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(newPortfolio).setVolume(eq(1));
        verify(positionSetter).isThereEnoughMoneyForWrite(isA(Portfolio.class));
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
    }

    /**
     * Method under test:
     * {@link PortfoliosService#finalOperationsForDirectOrReverseWritePortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testFinalOperationsForDirectOrReverseWritePortfolioIfItIsContained() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.save(Mockito.<Portfolio>any())).thenReturn(portfolio);
        doNothing().when(portfoliosRepository).delete(Mockito.<Portfolio>any());
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option2);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option3 = new Option();
        option3.setBuyCollateral(10.0d);
        option3.setDaysToMaturity(1);
        option3.setId("42");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(10.0d);
        option3.setStepPrice(10.0d);
        option3.setStrike(1);
        option3.setType("Type");
        option3.setVolatility(10.0d);
        option3.setWriteCollateral(10.0d);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(10.0d);
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(10.0d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("iloveyou");
        owner3.setRole("Role");
        owner3.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option3);
        oldPortfolio.setOwner(owner3);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act
        portfoliosService.finalOperationsForDirectOrReverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);

        // Assert
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(portfoliosRepository).delete(isA(Portfolio.class));
        verify(portfoliosRepository).save(isA(Portfolio.class));
    }

    /**
     * Method under test:
     * {@link PortfoliosService#reverseWritePortfolioIfItIsContained(Portfolio, Portfolio)}
     */
    @Test
    public void testReverseWritePortfolioIfItIsContained() {
        // Arrange
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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);
        when(portfoliosRepository.save(Mockito.<Portfolio>any())).thenReturn(portfolio);
        when(positionSetter.countPriceInRur(Mockito.<Portfolio>any())).thenReturn(10.0d);
        when(positionSetter.updateVariatMargin(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(10.0d);
        owner2.setEmail("jane.doe@example.org");
        owner2.setId(1L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(10.0d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("iloveyou");
        owner2.setRole("Role");
        owner2.setUsername("janedoe");

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option2);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        Option option3 = new Option();
        option3.setBuyCollateral(10.0d);
        option3.setDaysToMaturity(1);
        option3.setId("42");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(10.0d);
        option3.setStepPrice(10.0d);
        option3.setStrike(1);
        option3.setType("Type");
        option3.setVolatility(10.0d);
        option3.setWriteCollateral(10.0d);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(10.0d);
        owner3.setEmail("jane.doe@example.org");
        owner3.setId(1L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(10.0d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("iloveyou");
        owner3.setRole("Role");
        owner3.setUsername("janedoe");

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option3);
        oldPortfolio.setOwner(owner3);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

        // Act
        portfoliosService.reverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);

        // Assert
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(positionSetter).countPriceInRur(isA(Portfolio.class));
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(positionSetter).updateVariatMargin(isA(Portfolio.class));
        verify(portfoliosRepository).save(isA(Portfolio.class));
    }

    /**
     * Method under test: {@link PortfoliosService#expirationCheck()}
     */
    @Test
    public void testExpirationCheck() {
        // Arrange
        when(portfoliosRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        portfoliosService.expirationCheck();

        // Assert that nothing has changed
        verify(portfoliosRepository).findAll();
    }

    /**
     * Method under test: {@link PortfoliosService#expirationCheck()}
     */
    @Test
    public void testExpirationCheck2() {
        // Arrange
        doNothing().when(entityManager).refresh(Mockito.<Object>any());

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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio);
        when(portfoliosRepository.findAll()).thenReturn(portfolioList);

        // Act
        portfoliosService.expirationCheck();

        // Assert
        verify(entityManager).refresh(isA(Object.class));
        verify(portfoliosRepository).findAll();
    }

    /**
     * Method under test: {@link PortfoliosService#expirationCheck()}
     */
    @Test
    public void testExpirationCheck3() {
        // Arrange
        doNothing().when(entityManager).refresh(Mockito.<Object>any());

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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(0.5d);
        option2.setDaysToMaturity(0);
        option2.setId("Converting Portfolio '{}' to optionDto. Result: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(0.5d);
        option2.setStepPrice(0.5d);
        option2.setStrike(0);
        option2.setType("Type");
        option2.setVolatility(0.5d);
        option2.setWriteCollateral(0.5d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(0.5d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(0.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Converting Portfolio '{}' to optionDto. Result: {}");
        owner2.setRole("Role");
        owner2.setUsername("Converting Portfolio '{}' to optionDto. Result: {}");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setCollateralWhenWasTrade(0.5d);
        portfolio2.setId(2L);
        portfolio2.setOption(option2);
        portfolio2.setOwner(owner2);
        portfolio2.setTradePrice(0.5d);
        portfolio2.setVariatMargin(0.5d);
        portfolio2.setVolatilityWhenWasTrade(0.5d);
        portfolio2.setVolume(0);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio2);
        portfolioList.add(portfolio);
        doNothing().when(portfoliosRepository).delete(Mockito.<Portfolio>any());
        when(portfoliosRepository.findAll()).thenReturn(portfolioList);
        doNothing().when(personsService).save(Mockito.<Person>any());
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        // Act
        portfoliosService.expirationCheck();

        // Assert
        verify(entityManager, atLeast(1)).refresh(Mockito.<Object>any());
        verify(portfoliosRepository).findAll();
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(personsService).save(isA(Person.class));
        verify(portfoliosRepository).delete(isA(Portfolio.class));
    }

    /**
     * Method under test: {@link PortfoliosService#expirationCheck()}
     */
    @Test
    public void testExpirationCheck4() {
        // Arrange
        doNothing().when(entityManager).refresh(Mockito.<Object>any());

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

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(10.0d);
        portfolio.setId(1L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(10.0d);
        portfolio.setVariatMargin(10.0d);
        portfolio.setVolatilityWhenWasTrade(10.0d);
        portfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(0.5d);
        option2.setDaysToMaturity(0);
        option2.setId("Converting Portfolio '{}' to optionDto. Result: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(0.5d);
        option2.setStepPrice(0.5d);
        option2.setStrike(0);
        option2.setType("Type");
        option2.setVolatility(0.5d);
        option2.setWriteCollateral(0.5d);

        Person owner2 = new Person();
        owner2.setActivationCode("Activation Code");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(0.5d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(0.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Converting Portfolio '{}' to optionDto. Result: {}");
        owner2.setRole("Role");
        owner2.setUsername("Converting Portfolio '{}' to optionDto. Result: {}");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setCollateralWhenWasTrade(0.5d);
        portfolio2.setId(2L);
        portfolio2.setOption(option2);
        portfolio2.setOwner(owner2);
        portfolio2.setTradePrice(0.5d);
        portfolio2.setVariatMargin(0.5d);
        portfolio2.setVolatilityWhenWasTrade(0.5d);
        portfolio2.setVolume(0);

        Option option3 = new Option();
        option3.setBuyCollateral(-0.5d);
        option3.setDaysToMaturity(-1);
        option3.setId("Portfolio {} will be deleted");
        option3.setPortfolio(new ArrayList<>());
        option3.setPrice(-0.5d);
        option3.setStepPrice(-0.5d);
        option3.setStrike(-1);
        option3.setType("Type");
        option3.setVolatility(-0.5d);
        option3.setWriteCollateral(-0.5d);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(-0.5d);
        owner3.setEmail("prof.einstein@example.org");
        owner3.setId(3L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(-0.5d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("Portfolio {} will be deleted");
        owner3.setRole("Role");
        owner3.setUsername("Portfolio {} will be deleted");

        Portfolio portfolio3 = new Portfolio();
        portfolio3.setCollateralWhenWasTrade(-0.5d);
        portfolio3.setId(3L);
        portfolio3.setOption(option3);
        portfolio3.setOwner(owner3);
        portfolio3.setTradePrice(-0.5d);
        portfolio3.setVariatMargin(-0.5d);
        portfolio3.setVolatilityWhenWasTrade(-0.5d);
        portfolio3.setVolume(-1);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio3);
        portfolioList.add(portfolio2);
        portfolioList.add(portfolio);
        doNothing().when(portfoliosRepository).delete(Mockito.<Portfolio>any());
        when(portfoliosRepository.findAll()).thenReturn(portfolioList);
        doNothing().when(personsService).save(Mockito.<Person>any());
        when(positionSetter.updateVariatMargin(Mockito.<Portfolio>any())).thenReturn(10.0d);
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());

        // Act
        portfoliosService.expirationCheck();

        // Assert
        verify(entityManager, atLeast(1)).refresh(Mockito.<Object>any());
        verify(portfoliosRepository).findAll();
        verify(journalService, atLeast(1)).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());
        verify(personsService, atLeast(1)).save(Mockito.<Person>any());
        verify(positionSetter).updateVariatMargin(isA(Portfolio.class));
        verify(portfoliosRepository, atLeast(1)).delete(Mockito.<Portfolio>any());
    }

    /**
     * Method under test: {@link PortfoliosService#marginCallCheck()}
     */
    @Test
    public void testMarginCallCheck() {
        // Arrange
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(new ArrayList<>());

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
        when(personsService.getCurrentPerson()).thenReturn(person);

        // Act
        portfoliosService.marginCallCheck();

        // Assert that nothing has changed
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
    }

    /**
     * Method under test: {@link PortfoliosService#marginCallCheck()}
     */
    @Test
    public void testMarginCallCheck2() throws IOException, MessagingException {
        // Arrange
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(new ArrayList<>());

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(0.5d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(personsService.getCurrentPerson()).thenReturn(person);
        doNothing().when(smtpMailSender).sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(textConverter.readMailTextFromFile(Mockito.<String>any())).thenReturn("jane.doe@example.org");

        // Act
        portfoliosService.marginCallCheck();

        // Assert
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
        verify(textConverter).readMailTextFromFile(eq("${pathToMarginCallMail}"));
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Margin call"), eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link PortfoliosService#marginCallCheck()}
     */
    @Test
    public void testMarginCallCheck3() throws IOException, MessagingException {
        // Arrange
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(new ArrayList<>());

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(0.5d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(personsService.getCurrentPerson()).thenReturn(person);
        doThrow(new MessagingException()).when(smtpMailSender)
                .sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(textConverter.readMailTextFromFile(Mockito.<String>any())).thenReturn("jane.doe@example.org");

        // Act and Assert
        thrown.expect(RuntimeException.class);
        portfoliosService.marginCallCheck();
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
        verify(textConverter).readMailTextFromFile(eq("${pathToMarginCallMail}"));
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Margin call"), eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link PortfoliosService#marginCallCheck()}
     */
    @Test
    public void testMarginCallCheck4() throws IOException, MessagingException {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(5.0d);
        option.setDaysToMaturity(1);
        option.setId("Owner: {}, options in portfolio: {}");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(5.0d);
        option.setStepPrice(5.0d);
        option.setStrike(1);
        option.setType("Portfolio with min variat margin: {}");
        option.setVolatility(5.0d);
        option.setWriteCollateral(5.0d);

        Person owner = new Person();
        owner.setActivationCode("Portfolio with min variat margin: {}");
        owner.setActive(false);
        owner.setCurrentNetPosition(5.0d);
        owner.setEmail("john.smith@example.org");
        owner.setId(2L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(5.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("Owner: {}, options in portfolio: {}");
        owner.setRole("Portfolio with min variat margin: {}");
        owner.setUsername("Owner: {}, options in portfolio: {}");

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(5.0d);
        portfolio.setId(2L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(5.0d);
        portfolio.setVariatMargin(5.0d);
        portfolio.setVolatilityWhenWasTrade(5.0d);
        portfolio.setVolume(1);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio);
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(portfolioList);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(0.5d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        doNothing().when(personsService).save(Mockito.<Person>any());
        when(personsService.getCurrentPerson()).thenReturn(person);
        doNothing().when(positionSetter).updateAllVariatMargin();
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());
        doNothing().when(smtpMailSender).sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(textConverter.readMailTextFromFile(Mockito.<String>any())).thenReturn("jane.doe@example.org");

        // Act
        portfoliosService.marginCallCheck();

        // Assert
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(personsService).getCurrentPerson();
        verify(personsService).save(isA(Person.class));
        verify(positionSetter).updateAllVariatMargin();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(textConverter).readMailTextFromFile(eq("${pathToMarginCallMail}"));
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Margin call"), eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link PortfoliosService#marginCallCheck()}
     */
    @Test
    public void testMarginCallCheck5() throws IOException, MessagingException {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(5.0d);
        option.setDaysToMaturity(1);
        option.setId("Owner: {}, options in portfolio: {}");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(5.0d);
        option.setStepPrice(5.0d);
        option.setStrike(1);
        option.setType("Portfolio with min variat margin: {}");
        option.setVolatility(5.0d);
        option.setWriteCollateral(5.0d);

        Person owner = new Person();
        owner.setActivationCode("Portfolio with min variat margin: {}");
        owner.setActive(false);
        owner.setCurrentNetPosition(5.0d);
        owner.setEmail("john.smith@example.org");
        owner.setId(2L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(5.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("Owner: {}, options in portfolio: {}");
        owner.setRole("Portfolio with min variat margin: {}");
        owner.setUsername("Owner: {}, options in portfolio: {}");

        Portfolio portfolio = new Portfolio();
        portfolio.setCollateralWhenWasTrade(5.0d);
        portfolio.setId(2L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(5.0d);
        portfolio.setVariatMargin(5.0d);
        portfolio.setVolatilityWhenWasTrade(5.0d);
        portfolio.setVolume(1);

        Option option2 = new Option();
        option2.setBuyCollateral(5.5d);
        option2.setDaysToMaturity(0);
        option2.setId("Portfolio with min variat margin: {}");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(5.5d);
        option2.setStepPrice(5.5d);
        option2.setStrike(0);
        option2.setType("Converting Portfolio '{}' to optionDto. Result: {}");
        option2.setVolatility(5.5d);
        option2.setWriteCollateral(5.5d);

        Person owner2 = new Person();
        owner2.setActivationCode("Converting Portfolio '{}' to optionDto. Result: {}");
        owner2.setActive(true);
        owner2.setCurrentNetPosition(5.5d);
        owner2.setEmail("prof.einstein@example.org");
        owner2.setId(3L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(5.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Portfolio with min variat margin: {}");
        owner2.setRole("Converting Portfolio '{}' to optionDto. Result: {}");
        owner2.setUsername("Portfolio with min variat margin: {}");

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setCollateralWhenWasTrade(5.5d);
        portfolio2.setId(3L);
        portfolio2.setOption(option2);
        portfolio2.setOwner(owner2);
        portfolio2.setTradePrice(5.5d);
        portfolio2.setVariatMargin(5.5d);
        portfolio2.setVolatilityWhenWasTrade(5.5d);
        portfolio2.setVolume(0);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio2);
        portfolioList.add(portfolio);
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(portfolioList);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(0.5d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        doNothing().when(personsService).save(Mockito.<Person>any());
        when(personsService.getCurrentPerson()).thenReturn(person);
        doNothing().when(positionSetter).updateAllVariatMargin();
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());
        doNothing().when(smtpMailSender).sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(textConverter.readMailTextFromFile(Mockito.<String>any())).thenReturn("jane.doe@example.org");

        // Act
        portfoliosService.marginCallCheck();

        // Assert
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(personsService).getCurrentPerson();
        verify(personsService).save(isA(Person.class));
        verify(positionSetter).updateAllVariatMargin();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(textConverter).readMailTextFromFile(eq("${pathToMarginCallMail}"));
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Margin call"), eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link PortfoliosService#marginCallCheck()}
     */
    @Test
    public void testMarginCallCheck6() throws IOException, MessagingException {
        // Arrange
        Option option = new Option();
        option.setBuyCollateral(5.0d);
        option.setDaysToMaturity(1);
        option.setId("Owner: {}, options in portfolio: {}");
        option.setPortfolio(new ArrayList<>());
        option.setPrice(5.0d);
        option.setStepPrice(5.0d);
        option.setStrike(1);
        option.setType("Portfolio with min variat margin: {}");
        option.setVolatility(5.0d);
        option.setWriteCollateral(5.0d);

        Person owner = new Person();
        owner.setActivationCode("Portfolio with min variat margin: {}");
        owner.setActive(false);
        owner.setCurrentNetPosition(5.0d);
        owner.setEmail("john.smith@example.org");
        owner.setId(2L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(5.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("Owner: {}, options in portfolio: {}");
        owner.setRole("Portfolio with min variat margin: {}");
        owner.setUsername("Owner: {}, options in portfolio: {}");

        Option option2 = new Option();
        option2.setBuyCollateral(10.0d);
        option2.setDaysToMaturity(1);
        option2.setId("42");
        option2.setPortfolio(new ArrayList<>());
        option2.setPrice(10.0d);
        option2.setStepPrice(10.0d);
        option2.setStrike(1);
        option2.setType("Type");
        option2.setVolatility(10.0d);
        option2.setWriteCollateral(10.0d);
        Portfolio portfolio = mock(Portfolio.class);
        when(portfolio.getVariatMargin()).thenReturn(10.0d);
        when(portfolio.getOption()).thenReturn(option2);
        when(portfolio.getVolume()).thenReturn(-1);
        doNothing().when(portfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(portfolio).setId(anyLong());
        doNothing().when(portfolio).setOption(Mockito.<Option>any());
        doNothing().when(portfolio).setOwner(Mockito.<Person>any());
        doNothing().when(portfolio).setTradePrice(anyDouble());
        doNothing().when(portfolio).setVariatMargin(anyDouble());
        doNothing().when(portfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(portfolio).setVolume(anyInt());
        portfolio.setCollateralWhenWasTrade(5.0d);
        portfolio.setId(2L);
        portfolio.setOption(option);
        portfolio.setOwner(owner);
        portfolio.setTradePrice(5.0d);
        portfolio.setVariatMargin(5.0d);
        portfolio.setVolatilityWhenWasTrade(5.0d);
        portfolio.setVolume(1);

        ArrayList<Portfolio> portfolioList = new ArrayList<>();
        portfolioList.add(portfolio);
        when(portfoliosRepository.findAllByOwner(Mockito.<Person>any())).thenReturn(portfolioList);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(0.5d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        doNothing().when(personsService).save(Mockito.<Person>any());
        when(personsService.getCurrentPerson()).thenReturn(person);
        doNothing().when(positionSetter).updateAllVariatMargin();
        doNothing().when(positionSetter).updateCurrentNetPositionAndOpenLimit();
        doNothing().when(journalService).addJournal(Mockito.<Portfolio>any(), Mockito.<OptionDto>any());
        doNothing().when(smtpMailSender).sendHtml(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(textConverter.readMailTextFromFile(Mockito.<String>any())).thenReturn("jane.doe@example.org");

        // Act
        portfoliosService.marginCallCheck();

        // Assert
        verify(portfolio, atLeast(1)).getOption();
        verify(portfolio).getVariatMargin();
        verify(portfolio, atLeast(1)).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(5.0d));
        verify(portfolio).setId(eq(2L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(5.0d));
        verify(portfolio, atLeast(1)).setVariatMargin(anyDouble());
        verify(portfolio).setVolatilityWhenWasTrade(eq(5.0d));
        verify(portfolio, atLeast(1)).setVolume(anyInt());
        verify(portfoliosRepository).findAllByOwner(isA(Person.class));
        verify(journalService).addJournal(isA(Portfolio.class), isA(OptionDto.class));
        verify(personsService).getCurrentPerson();
        verify(personsService).save(isA(Person.class));
        verify(positionSetter).updateAllVariatMargin();
        verify(positionSetter).updateCurrentNetPositionAndOpenLimit();
        verify(textConverter).readMailTextFromFile(eq("${pathToMarginCallMail}"));
        verify(smtpMailSender).sendHtml(eq("jane.doe@example.org"), eq("Margin call"), eq("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link PortfoliosService#reset()}
     */
    @Test
    public void testReset() {
        // Arrange
        doNothing().when(portfoliosRepository).deleteAllByOwner(Mockito.<Person>any());

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
        when(personsService.getCurrentPerson()).thenReturn(person);
        doNothing().when(journalRepository).deleteAllByOwner(Mockito.<Person>any());

        // Act
        portfoliosService.reset();

        // Assert
        verify(journalRepository).deleteAllByOwner(isA(Person.class));
        verify(portfoliosRepository).deleteAllByOwner(isA(Person.class));
        verify(personsService).getCurrentPerson();
    }
}
