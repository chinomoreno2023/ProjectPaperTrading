package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IAuthFacade;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = {PositionSetter.class})
@ExtendWith(SpringExtension.class)
class PositionSetterDiffblueTest {
    @MockBean
    private IAuthFacade iAuthFacade;

    @MockBean
    private EntityManager entityManager;

    @MockBean
    private PersonsService personsService;

    @MockBean
    private PortfoliosRepository portfoliosRepository;

    @Autowired
    private PositionSetter positionSetter;

    /**
     * Method under test: {@link PositionSetter#isThereEnoughMoneyForBuy(Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForBuy() {
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

        // Act and Assert
        assertTrue(positionSetter.isThereEnoughMoneyForBuy(portfolio));
    }

    /**
     * Method under test: {@link PositionSetter#isThereEnoughMoneyForBuy(Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForBuy2() {
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
        when(portfolio.getOwner()).thenReturn(person);
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
        boolean actualIsThereEnoughMoneyForBuyResult = positionSetter.isThereEnoughMoneyForBuy(portfolio);

        // Assert
        verify(portfolio).getOption();
        verify(portfolio).getOwner();
        verify(portfolio).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio).setVariatMargin(eq(10.0d));
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertTrue(actualIsThereEnoughMoneyForBuyResult);
    }

    /**
     * Method under test: {@link PositionSetter#isThereEnoughMoneyForBuy(Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForBuy3() {
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

        Option option2 = new Option();
        option2.setBuyCollateral(Double.NaN);
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
        when(portfolio.getOwner()).thenReturn(person);
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
        boolean actualIsThereEnoughMoneyForBuyResult = positionSetter.isThereEnoughMoneyForBuy(portfolio);

        // Assert
        verify(portfolio).getOption();
        verify(portfolio).getOwner();
        verify(portfolio).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio).setVariatMargin(eq(10.0d));
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertFalse(actualIsThereEnoughMoneyForBuyResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForWrite(Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForWrite() {
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

        // Act and Assert
        assertTrue(positionSetter.isThereEnoughMoneyForWrite(portfolio));
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForWrite(Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForWrite2() {
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
        when(portfolio.getOwner()).thenReturn(person);
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
        boolean actualIsThereEnoughMoneyForWriteResult = positionSetter.isThereEnoughMoneyForWrite(portfolio);

        // Assert
        verify(portfolio).getOption();
        verify(portfolio).getOwner();
        verify(portfolio).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio).setVariatMargin(eq(10.0d));
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertTrue(actualIsThereEnoughMoneyForWriteResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForWrite(Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForWrite3() {
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
        option2.setWriteCollateral(Double.NaN);
        Portfolio portfolio = mock(Portfolio.class);
        when(portfolio.getVolume()).thenReturn(1);
        when(portfolio.getOption()).thenReturn(option2);
        when(portfolio.getOwner()).thenReturn(person);
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
        boolean actualIsThereEnoughMoneyForWriteResult = positionSetter.isThereEnoughMoneyForWrite(portfolio);

        // Assert
        verify(portfolio).getOption();
        verify(portfolio).getOwner();
        verify(portfolio).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio).setVariatMargin(eq(10.0d));
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertFalse(actualIsThereEnoughMoneyForWriteResult);
    }

    /**
     * Method under test: {@link PositionSetter#doEveningClearing()}
     */
    @Test
    void testDoEveningClearing() {
        // Arrange
        when(portfoliosRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        positionSetter.doEveningClearing();

        // Assert that nothing has changed
        verify(portfoliosRepository).findAll();
    }

    /**
     * Method under test: {@link PositionSetter#updateVariatMargin(Portfolio)}
     */
    @Test
    void testUpdateVariatMargin() {
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
        double actualUpdateVariatMarginResult = positionSetter.updateVariatMargin(portfolio);

        // Assert
        assertEquals(-10.0d, portfolio.getVariatMargin());
        assertEquals(-10.0d, actualUpdateVariatMarginResult);
    }

    /**
     * Method under test: {@link PositionSetter#updateVariatMargin(Portfolio)}
     */
    @Test
    void testUpdateVariatMargin2() {
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
        when(portfolio.getTradePrice()).thenReturn(10.0d);
        when(portfolio.getVariatMargin()).thenReturn(10.0d);
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
        double actualUpdateVariatMarginResult = positionSetter.updateVariatMargin(portfolio);

        // Assert
        verify(portfolio, atLeast(1)).getOption();
        verify(portfolio).getTradePrice();
        verify(portfolio).getVariatMargin();
        verify(portfolio, atLeast(1)).getVolume();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio, atLeast(1)).setVariatMargin(anyDouble());
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertEquals(10.0d, actualUpdateVariatMarginResult);
    }

    /**
     * Method under test: {@link PositionSetter#updateVariatMargin(Portfolio)}
     */
    @Test
    void testUpdateVariatMargin3() {
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
        when(portfolio.getVolume()).thenThrow(new IllegalArgumentException("Option ticker: {}"));
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

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> positionSetter.updateVariatMargin(portfolio));
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
    }

    /**
     * Method under test: {@link PositionSetter#checkBuyOrWrite(OptionDto)}
     */
    @Test
    void testCheckBuyOrWrite() {
        // Arrange
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

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> positionSetter.checkBuyOrWrite(optionDto));
    }

    /**
     * Method under test: {@link PositionSetter#checkBuyOrWrite(OptionDto)}
     */
    @Test
    void testCheckBuyOrWrite2() {
        // Arrange
        OptionDto optionDto = mock(OptionDto.class);
        when(optionDto.getBuyOrWrite()).thenReturn(1);
        doNothing().when(optionDto).setBuyCollateral(anyDouble());
        doNothing().when(optionDto).setBuyOrWrite(anyInt());
        doNothing().when(optionDto).setDaysToMaturity(anyInt());
        doNothing().when(optionDto).setId(Mockito.<String>any());
        doNothing().when(optionDto).setPrice(anyDouble());
        doNothing().when(optionDto).setStepPrice(anyDouble());
        doNothing().when(optionDto).setStrike(anyInt());
        doNothing().when(optionDto).setType(Mockito.<String>any());
        doNothing().when(optionDto).setVolatility(anyDouble());
        doNothing().when(optionDto).setVolume(anyInt());
        doNothing().when(optionDto).setWriteCollateral(anyDouble());
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
        boolean actualCheckBuyOrWriteResult = positionSetter.checkBuyOrWrite(optionDto);

        // Assert
        verify(optionDto).getBuyOrWrite();
        verify(optionDto).setBuyCollateral(eq(10.0d));
        verify(optionDto).setBuyOrWrite(eq(19088743));
        verify(optionDto).setDaysToMaturity(eq(1));
        verify(optionDto).setId(eq("42"));
        verify(optionDto).setPrice(eq(10.0d));
        verify(optionDto).setStepPrice(eq(10.0d));
        verify(optionDto).setStrike(eq(1));
        verify(optionDto).setType(eq("Type"));
        verify(optionDto).setVolatility(eq(10.0d));
        verify(optionDto).setVolume(eq(1));
        verify(optionDto).setWriteCollateral(eq(10.0d));
        assertTrue(actualCheckBuyOrWriteResult);
    }

    /**
     * Method under test: {@link PositionSetter#checkBuyOrWrite(OptionDto)}
     */
    @Test
    void testCheckBuyOrWrite3() {
        // Arrange
        OptionDto optionDto = mock(OptionDto.class);
        when(optionDto.getBuyOrWrite()).thenReturn(-1);
        doNothing().when(optionDto).setBuyCollateral(anyDouble());
        doNothing().when(optionDto).setBuyOrWrite(anyInt());
        doNothing().when(optionDto).setDaysToMaturity(anyInt());
        doNothing().when(optionDto).setId(Mockito.<String>any());
        doNothing().when(optionDto).setPrice(anyDouble());
        doNothing().when(optionDto).setStepPrice(anyDouble());
        doNothing().when(optionDto).setStrike(anyInt());
        doNothing().when(optionDto).setType(Mockito.<String>any());
        doNothing().when(optionDto).setVolatility(anyDouble());
        doNothing().when(optionDto).setVolume(anyInt());
        doNothing().when(optionDto).setWriteCollateral(anyDouble());
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
        boolean actualCheckBuyOrWriteResult = positionSetter.checkBuyOrWrite(optionDto);

        // Assert
        verify(optionDto).getBuyOrWrite();
        verify(optionDto).setBuyCollateral(eq(10.0d));
        verify(optionDto).setBuyOrWrite(eq(19088743));
        verify(optionDto).setDaysToMaturity(eq(1));
        verify(optionDto).setId(eq("42"));
        verify(optionDto).setPrice(eq(10.0d));
        verify(optionDto).setStepPrice(eq(10.0d));
        verify(optionDto).setStrike(eq(1));
        verify(optionDto).setType(eq("Type"));
        verify(optionDto).setVolatility(eq(10.0d));
        verify(optionDto).setVolume(eq(1));
        verify(optionDto).setWriteCollateral(eq(10.0d));
        assertFalse(actualCheckBuyOrWriteResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#checkDirectOrReverse(OptionDto, Portfolio)}
     */
    @Test
    void testCheckDirectOrReverse() {
        // Arrange
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

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> positionSetter.checkDirectOrReverse(optionDto, portfolio));
    }

    /**
     * Method under test:
     * {@link PositionSetter#checkDirectOrReverse(OptionDto, Portfolio)}
     */
    @Test
    void testCheckDirectOrReverse2() {
        // Arrange
        OptionDto optionDto = mock(OptionDto.class);
        when(optionDto.getBuyOrWrite()).thenReturn(1);
        doNothing().when(optionDto).setBuyCollateral(anyDouble());
        doNothing().when(optionDto).setBuyOrWrite(anyInt());
        doNothing().when(optionDto).setDaysToMaturity(anyInt());
        doNothing().when(optionDto).setId(Mockito.<String>any());
        doNothing().when(optionDto).setPrice(anyDouble());
        doNothing().when(optionDto).setStepPrice(anyDouble());
        doNothing().when(optionDto).setStrike(anyInt());
        doNothing().when(optionDto).setType(Mockito.<String>any());
        doNothing().when(optionDto).setVolatility(anyDouble());
        doNothing().when(optionDto).setVolume(anyInt());
        doNothing().when(optionDto).setWriteCollateral(anyDouble());
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
        boolean actualCheckDirectOrReverseResult = positionSetter.checkDirectOrReverse(optionDto, portfolio);

        // Assert
        verify(optionDto).getBuyOrWrite();
        verify(optionDto).setBuyCollateral(eq(10.0d));
        verify(optionDto).setBuyOrWrite(eq(19088743));
        verify(optionDto).setDaysToMaturity(eq(1));
        verify(optionDto).setId(eq("42"));
        verify(optionDto).setPrice(eq(10.0d));
        verify(optionDto).setStepPrice(eq(10.0d));
        verify(optionDto).setStrike(eq(1));
        verify(optionDto).setType(eq("Type"));
        verify(optionDto).setVolatility(eq(10.0d));
        verify(optionDto).setVolume(eq(1));
        verify(optionDto).setWriteCollateral(eq(10.0d));
        assertTrue(actualCheckDirectOrReverseResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#checkDirectOrReverse(OptionDto, Portfolio)}
     */
    @Test
    void testCheckDirectOrReverse3() {
        // Arrange
        OptionDto optionDto = mock(OptionDto.class);
        when(optionDto.getBuyOrWrite()).thenReturn(-1);
        doNothing().when(optionDto).setBuyCollateral(anyDouble());
        doNothing().when(optionDto).setBuyOrWrite(anyInt());
        doNothing().when(optionDto).setDaysToMaturity(anyInt());
        doNothing().when(optionDto).setId(Mockito.<String>any());
        doNothing().when(optionDto).setPrice(anyDouble());
        doNothing().when(optionDto).setStepPrice(anyDouble());
        doNothing().when(optionDto).setStrike(anyInt());
        doNothing().when(optionDto).setType(Mockito.<String>any());
        doNothing().when(optionDto).setVolatility(anyDouble());
        doNothing().when(optionDto).setVolume(anyInt());
        doNothing().when(optionDto).setWriteCollateral(anyDouble());
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
        boolean actualCheckDirectOrReverseResult = positionSetter.checkDirectOrReverse(optionDto, portfolio);

        // Assert
        verify(optionDto, atLeast(1)).getBuyOrWrite();
        verify(optionDto).setBuyCollateral(eq(10.0d));
        verify(optionDto).setBuyOrWrite(eq(19088743));
        verify(optionDto).setDaysToMaturity(eq(1));
        verify(optionDto).setId(eq("42"));
        verify(optionDto).setPrice(eq(10.0d));
        verify(optionDto).setStepPrice(eq(10.0d));
        verify(optionDto).setStrike(eq(1));
        verify(optionDto).setType(eq("Type"));
        verify(optionDto).setVolatility(eq(10.0d));
        verify(optionDto).setVolume(eq(1));
        verify(optionDto).setWriteCollateral(eq(10.0d));
        assertFalse(actualCheckDirectOrReverseResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseBuy(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseBuy() {
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

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        // Act and Assert
        assertTrue(positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio));
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseBuy(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseBuy2() {
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
        Portfolio oldPortfolio = mock(Portfolio.class);
        when(oldPortfolio.getVolume()).thenReturn(1);
        when(oldPortfolio.getOwner()).thenReturn(person);
        when(oldPortfolio.getOption()).thenReturn(option2);
        doNothing().when(oldPortfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setId(anyLong());
        doNothing().when(oldPortfolio).setOption(Mockito.<Option>any());
        doNothing().when(oldPortfolio).setOwner(Mockito.<Person>any());
        doNothing().when(oldPortfolio).setTradePrice(anyDouble());
        doNothing().when(oldPortfolio).setVariatMargin(anyDouble());
        doNothing().when(oldPortfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setVolume(anyInt());
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option3);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        // Act
        boolean actualIsThereEnoughMoneyForReverseBuyResult = positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio,
                newPortfolio);

        // Assert
        verify(oldPortfolio).getOption();
        verify(oldPortfolio).getOwner();
        verify(oldPortfolio, atLeast(1)).getVolume();
        verify(oldPortfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setId(eq(1L));
        verify(oldPortfolio).setOption(isA(Option.class));
        verify(oldPortfolio).setOwner(isA(Person.class));
        verify(oldPortfolio).setTradePrice(eq(10.0d));
        verify(oldPortfolio).setVariatMargin(eq(10.0d));
        verify(oldPortfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setVolume(eq(1));
        assertTrue(actualIsThereEnoughMoneyForReverseBuyResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseBuy(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseBuy3() {
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
        Portfolio oldPortfolio = mock(Portfolio.class);
        when(oldPortfolio.getVolume()).thenThrow(new IllegalArgumentException("foo"));
        when(oldPortfolio.getOption()).thenReturn(option2);
        doNothing().when(oldPortfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setId(anyLong());
        doNothing().when(oldPortfolio).setOption(Mockito.<Option>any());
        doNothing().when(oldPortfolio).setOwner(Mockito.<Person>any());
        doNothing().when(oldPortfolio).setTradePrice(anyDouble());
        doNothing().when(oldPortfolio).setVariatMargin(anyDouble());
        doNothing().when(oldPortfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setVolume(anyInt());
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option3);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        // Act and Assert
        assertThrows(IllegalArgumentException.class,
                () -> positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio));
        verify(oldPortfolio).getOption();
        verify(oldPortfolio).getVolume();
        verify(oldPortfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setId(eq(1L));
        verify(oldPortfolio).setOption(isA(Option.class));
        verify(oldPortfolio).setOwner(isA(Person.class));
        verify(oldPortfolio).setTradePrice(eq(10.0d));
        verify(oldPortfolio).setVariatMargin(eq(10.0d));
        verify(oldPortfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setVolume(eq(1));
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseBuy(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseBuy4() {
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

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(Double.NaN);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        Portfolio oldPortfolio = mock(Portfolio.class);
        when(oldPortfolio.getVolume()).thenReturn(1);
        when(oldPortfolio.getOwner()).thenReturn(person);
        when(oldPortfolio.getOption()).thenReturn(option2);
        doNothing().when(oldPortfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setId(anyLong());
        doNothing().when(oldPortfolio).setOption(Mockito.<Option>any());
        doNothing().when(oldPortfolio).setOwner(Mockito.<Person>any());
        doNothing().when(oldPortfolio).setTradePrice(anyDouble());
        doNothing().when(oldPortfolio).setVariatMargin(anyDouble());
        doNothing().when(oldPortfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setVolume(anyInt());
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option3);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        // Act
        boolean actualIsThereEnoughMoneyForReverseBuyResult = positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio,
                newPortfolio);

        // Assert
        verify(oldPortfolio).getOption();
        verify(oldPortfolio).getOwner();
        verify(oldPortfolio, atLeast(1)).getVolume();
        verify(oldPortfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setId(eq(1L));
        verify(oldPortfolio).setOption(isA(Option.class));
        verify(oldPortfolio).setOwner(isA(Person.class));
        verify(oldPortfolio).setTradePrice(eq(10.0d));
        verify(oldPortfolio).setVariatMargin(eq(10.0d));
        verify(oldPortfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setVolume(eq(1));
        assertFalse(actualIsThereEnoughMoneyForReverseBuyResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseWrite(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseWrite() {
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

        Portfolio oldPortfolio = new Portfolio();
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        // Act and Assert
        assertTrue(positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio));
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseWrite(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseWrite2() {
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
        Portfolio oldPortfolio = mock(Portfolio.class);
        when(oldPortfolio.getVolume()).thenReturn(1);
        when(oldPortfolio.getOption()).thenReturn(option2);
        doNothing().when(oldPortfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setId(anyLong());
        doNothing().when(oldPortfolio).setOption(Mockito.<Option>any());
        doNothing().when(oldPortfolio).setOwner(Mockito.<Person>any());
        doNothing().when(oldPortfolio).setTradePrice(anyDouble());
        doNothing().when(oldPortfolio).setVariatMargin(anyDouble());
        doNothing().when(oldPortfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setVolume(anyInt());
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option3);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        // Act
        boolean actualIsThereEnoughMoneyForReverseWriteResult = positionSetter
                .isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio);

        // Assert
        verify(oldPortfolio).getOption();
        verify(oldPortfolio, atLeast(1)).getVolume();
        verify(oldPortfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setId(eq(1L));
        verify(oldPortfolio).setOption(isA(Option.class));
        verify(oldPortfolio).setOwner(isA(Person.class));
        verify(oldPortfolio).setTradePrice(eq(10.0d));
        verify(oldPortfolio).setVariatMargin(eq(10.0d));
        verify(oldPortfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setVolume(eq(1));
        assertTrue(actualIsThereEnoughMoneyForReverseWriteResult);
    }

    /**
     * Method under test:
     * {@link PositionSetter#isThereEnoughMoneyForReverseWrite(Portfolio, Portfolio)}
     */
    @Test
    void testIsThereEnoughMoneyForReverseWrite3() {
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
        Portfolio oldPortfolio = mock(Portfolio.class);
        when(oldPortfolio.getVolume()).thenReturn(-1);
        when(oldPortfolio.getOption()).thenReturn(option2);
        doNothing().when(oldPortfolio).setCollateralWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setId(anyLong());
        doNothing().when(oldPortfolio).setOption(Mockito.<Option>any());
        doNothing().when(oldPortfolio).setOwner(Mockito.<Person>any());
        doNothing().when(oldPortfolio).setTradePrice(anyDouble());
        doNothing().when(oldPortfolio).setVariatMargin(anyDouble());
        doNothing().when(oldPortfolio).setVolatilityWhenWasTrade(anyDouble());
        doNothing().when(oldPortfolio).setVolume(anyInt());
        oldPortfolio.setCollateralWhenWasTrade(10.0d);
        oldPortfolio.setId(1L);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);
        oldPortfolio.setTradePrice(10.0d);
        oldPortfolio.setVariatMargin(10.0d);
        oldPortfolio.setVolatilityWhenWasTrade(10.0d);
        oldPortfolio.setVolume(1);

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

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setCollateralWhenWasTrade(10.0d);
        newPortfolio.setId(1L);
        newPortfolio.setOption(option3);
        newPortfolio.setOwner(owner2);
        newPortfolio.setTradePrice(10.0d);
        newPortfolio.setVariatMargin(10.0d);
        newPortfolio.setVolatilityWhenWasTrade(10.0d);
        newPortfolio.setVolume(1);

        // Act
        boolean actualIsThereEnoughMoneyForReverseWriteResult = positionSetter
                .isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio);

        // Assert
        verify(oldPortfolio).getOption();
        verify(oldPortfolio, atLeast(1)).getVolume();
        verify(oldPortfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setId(eq(1L));
        verify(oldPortfolio).setOption(isA(Option.class));
        verify(oldPortfolio).setOwner(isA(Person.class));
        verify(oldPortfolio).setTradePrice(eq(10.0d));
        verify(oldPortfolio).setVariatMargin(eq(10.0d));
        verify(oldPortfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(oldPortfolio).setVolume(eq(1));
        assertFalse(actualIsThereEnoughMoneyForReverseWriteResult);
    }

    /**
     * Method under test: {@link PositionSetter#countPriceInRur(Option)}
     */
    @Test
    void testCountPriceInRur() {
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

        // Act and Assert
        assertEquals(0.0d, positionSetter.countPriceInRur(option));
    }

    /**
     * Method under test: {@link PositionSetter#countPriceInRur(Option)}
     */
    @Test
    void testCountPriceInRur2() {
        // Arrange
        Option option = mock(Option.class);
        when(option.getId()).thenReturn("42");
        doNothing().when(option).setBuyCollateral(anyDouble());
        doNothing().when(option).setDaysToMaturity(anyInt());
        doNothing().when(option).setId(Mockito.<String>any());
        doNothing().when(option).setPortfolio(Mockito.<List<Portfolio>>any());
        doNothing().when(option).setPrice(anyDouble());
        doNothing().when(option).setStepPrice(anyDouble());
        doNothing().when(option).setStrike(anyInt());
        doNothing().when(option).setType(Mockito.<String>any());
        doNothing().when(option).setVolatility(anyDouble());
        doNothing().when(option).setWriteCollateral(anyDouble());
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

        // Act
        double actualCountPriceInRurResult = positionSetter.countPriceInRur(option);

        // Assert
        verify(option, atLeast(1)).getId();
        verify(option).setBuyCollateral(eq(10.0d));
        verify(option).setDaysToMaturity(eq(1));
        verify(option).setId(eq("42"));
        verify(option).setPortfolio(isA(List.class));
        verify(option).setPrice(eq(10.0d));
        verify(option).setStepPrice(eq(10.0d));
        verify(option).setStrike(eq(1));
        verify(option).setType(eq("Type"));
        verify(option).setVolatility(eq(10.0d));
        verify(option).setWriteCollateral(eq(10.0d));
        assertEquals(0.0d, actualCountPriceInRurResult);
    }

    /**
     * Method under test: {@link PositionSetter#countPriceInRur(Portfolio)}
     */
    @Test
    void testCountPriceInRur3() {
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

        // Act and Assert
        assertEquals(0.0d, positionSetter.countPriceInRur(portfolio));
    }

    /**
     * Method under test: {@link PositionSetter#countPriceInRur(Portfolio)}
     */
    @Test
    void testCountPriceInRur4() {
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
        double actualCountPriceInRurResult = positionSetter.countPriceInRur(portfolio);

        // Assert
        verify(portfolio, atLeast(1)).getOption();
        verify(portfolio).setCollateralWhenWasTrade(eq(10.0d));
        verify(portfolio).setId(eq(1L));
        verify(portfolio).setOption(isA(Option.class));
        verify(portfolio).setOwner(isA(Person.class));
        verify(portfolio).setTradePrice(eq(10.0d));
        verify(portfolio).setVariatMargin(eq(10.0d));
        verify(portfolio).setVolatilityWhenWasTrade(eq(10.0d));
        verify(portfolio).setVolume(eq(1));
        assertEquals(0.0d, actualCountPriceInRurResult);
    }
}
