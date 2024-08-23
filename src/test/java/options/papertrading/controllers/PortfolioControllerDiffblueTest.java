package options.papertrading.controllers;

import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.OptionFacade;
import options.papertrading.facade.PersonFacade;
import options.papertrading.facade.PortfolioFacade;
import options.papertrading.facade.interfaces.IOptionFacade;
import options.papertrading.facade.interfaces.IPersonFacade;
import options.papertrading.facade.interfaces.IPortfolioFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PortfolioController.class, PortfolioFacade.class, OptionFacade.class,
        PersonFacade.class})
@ExtendWith(SpringExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class PortfolioControllerDiffblueTest {
    @MockBean
    private IOptionFacade iOptionFacade;

    @MockBean
    private IPersonFacade iPersonFacade;

    @MockBean
    private IPortfolioFacade iPortfolioFacade;

    @Autowired
    private PortfolioController portfolioController;

    /**
     * Method under test: {@link PortfolioController#openTestPage()}
     */
    @Test
    void testOpenTestPage() throws Exception {
        // Arrange
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test: {@link PortfolioController#showAllPortfolios(Model)}
     */
    @Test
    void testShowAllPortfolios() throws Exception {
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
        doNothing().when(iPersonFacade).save(Mockito.<Person>any());
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portfolio");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("memo/memoForRegistered"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("memo/memoForRegistered"));
    }

    /**
     * Method under test: {@link PortfolioController#showAllPortfolios(Model)}
     */
    @Test
    void testShowAllPortfolios2() throws Exception {
        // Arrange
        when(iPortfolioFacade.showAllPortfolios()).thenReturn(new ArrayList<>());
        when(iOptionFacade.showOptionsFromCurrentPortfolios(Mockito.<List<PortfolioDto>>any()))
                .thenReturn(new ArrayList<>());

        Person person = new Person();
        person.setActivationCode(null);
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
        doNothing().when(iPersonFacade).save(Mockito.<Person>any());
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portfolio");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(3))
                .andExpect(MockMvcResultMatchers.model().attributeExists("options", "owner", "portfolios"))
                .andExpect(MockMvcResultMatchers.view().name("portfolio/portfolio"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("portfolio/portfolio"));
    }

    /**
     * Method under test: {@link PortfolioController#showAllPortfolios(Model)}
     */
    @Test
    void testShowAllPortfolios3() throws Exception {
        // Arrange
        when(iPortfolioFacade.showAllPortfolios()).thenReturn(new ArrayList<>());
        when(iOptionFacade.showOptionsFromCurrentPortfolios(Mockito.<List<PortfolioDto>>any()))
                .thenReturn(new ArrayList<>());

        Person person = new Person();
        person.setActivationCode(null);
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
        doNothing().when(iPersonFacade).save(Mockito.<Person>any());
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test: {@link PortfolioController#addPortfolio(String, int, int)}
     */
    @Test
    void testAddPortfolio() throws Exception {
        // Arrange
        doNothing().when(iPortfolioFacade).addPortfolio(Mockito.<String>any(), anyInt(), anyInt());
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/portfolio/confirmation");
        MockHttpServletRequestBuilder paramResult = postResult.param("buyOrWrite", String.valueOf(1)).param("id", "foo");
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("volume", String.valueOf(1));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???????? ??????? ?????????"));
    }

    /**
     * Method under test: {@link PortfolioController#addPortfolio(String, int, int)}
     */
    @Test
    void testAddPortfolio2() throws Exception {
        // Arrange
        doNothing().when(iPortfolioFacade).addPortfolio(Mockito.<String>any(), anyInt(), anyInt());
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/portfolio/confirmation");
        MockHttpServletRequestBuilder paramResult = postResult.param("buyOrWrite", String.valueOf(1)).param("id", "foo");
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("volume", String.valueOf(0));

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("????? ?? ????? ???? ????? ????"));
    }

    /**
     * Method under test: {@link PortfolioController#addPortfolio(String, int, int)}
     */
    @Test
    void testAddPortfolio3() throws Exception {
        // Arrange
        doThrow(new InsufficientFundsException()).when(iPortfolioFacade)
                .addPortfolio(Mockito.<String>any(), anyInt(), anyInt());
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/portfolio/confirmation");
        MockHttpServletRequestBuilder paramResult = postResult.param("buyOrWrite", String.valueOf(1)).param("id", "foo");
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("volume", String.valueOf(1));

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???????????? ???????"));
    }

    /**
     * Method under test: {@link PortfolioController#reset()}
     */
    @Test
    void testReset() throws Exception {
        // Arrange
        doNothing().when(iPortfolioFacade).reset();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/portfolio/reset");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?????? ???????? ???? ????????"));
    }

    /**
     * Method under test: {@link PortfolioController#showOptionsList(String, Model)}
     */
    @Test
    void testShowOptionsList() throws Exception {
        // Arrange
        when(iOptionFacade.showOptionsListByPrefix(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portfolio/options")
                .param("selectedValue", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("options"))
                .andExpect(MockMvcResultMatchers.view().name("options/options"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("options/options"));
    }
}
