package options.papertrading.controllers;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.OptionFacade;
import options.papertrading.facade.PersonFacade;
import options.papertrading.facade.PortfolioFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortfolioFacade portfolioFacade;

    @Mock
    private OptionFacade optionFacade;

    @Mock
    private PersonFacade personFacade;

    @InjectMocks
    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();
    }

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertTrue(PortfolioController.class.isAnnotationPresent(Controller.class));
        assertTrue(PortfolioController.class.isAnnotationPresent(RequestMapping.class));
        assertEquals("/portfolio",
                PortfolioController.class.getAnnotation(RequestMapping.class).value()[0]);
    }

    @Test
    void showAllPortfolios_ShouldReturnPortfolioView_WhenActivationCodeNull() throws Exception {
        Person owner = new Person();
        owner.setActivationCode(null);
        List<PortfolioDto> portfolios = List.of(new PortfolioDto());
        List<OptionDto> options = List.of(new OptionDto());

        when(personFacade.getCurrentPerson()).thenReturn(owner);
        when(portfolioFacade.showAllPortfolios()).thenReturn(portfolios);
        when(optionFacade.showOptionsFromCurrentPortfolios(portfolios)).thenReturn(options);

        mockMvc.perform(get("/portfolio"))
                .andExpectAll(
                        status().isOk(),
                        view().name("portfolio/portfolio"),
                        model().attribute("portfolios", portfolios),
                        model().attribute("options", options),
                        model().attribute("owner", owner)
                );
    }

    @Test
    void showAllPortfolios_ShouldReturnMemoView_WhenActivationCodeNotNull() throws Exception {
        Person owner = new Person();
        owner.setActivationCode("code");

        when(personFacade.getCurrentPerson()).thenReturn(owner);

        mockMvc.perform(get("/portfolio"))
                .andExpectAll(
                        status().isOk(),
                        view().name("memo/memoForRegistered")
                );

        verify(personFacade).save(owner);
        assertNull(owner.getActivationCode());
    }

    @Test
    void showOptionsList_ShouldReturnOptionsView() throws Exception {
        String selectedValue = "test";
        List<OptionDto> options = List.of(new OptionDto());

        when(optionFacade.showOptionsListByPrefix(selectedValue)).thenReturn(options);

        mockMvc.perform(get("/portfolio/options")
                        .param("selectedValue", selectedValue))
                .andExpectAll(
                        status().isOk(),
                        view().name("options/options"),
                        model().attribute("options", options)
                );
    }

    @Test
    void addPortfolio_ShouldReturnOk_WhenValidRequest() throws Exception {
        doNothing().when(portfolioFacade).addPortfolio(anyString(), anyInt(), anyInt());

        mockMvc.perform(post("/portfolio/confirmation")
                        .param("id", "OPT123")
                        .param("volume", "10")
                        .param("buyOrWrite", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Операция успешно выполнена")
                );
    }

    @Test
    void addPortfolio_ShouldReturnBadRequest_WhenVolumeInvalid() throws Exception {
        mockMvc.perform(post("/portfolio/confirmation")
                        .param("id", "OPT123")
                        .param("volume", "0")
                        .param("buyOrWrite", "1"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("Объём не может быть равен нулю")
                );
    }

    @Test
    void addPortfolio_ShouldReturnBadRequest_WhenInsufficientFunds() throws Exception {
        String errorMessage = "Недостаточно средств";
        doThrow(new InsufficientFundsException())
                .when(portfolioFacade).addPortfolio(anyString(), anyInt(), anyInt());

        mockMvc.perform(post("/portfolio/confirmation")
                        .param("id", "OPT123")
                        .param("volume", "10")
                        .param("buyOrWrite", "1"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string(errorMessage)
                );
    }

    @Test
    void reset_ShouldReturnOk_WhenSuccess() throws Exception {
        mockMvc.perform(post("/portfolio/reset"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Данные портфеля были сброшены")
                );

        verify(portfolioFacade).reset();
    }

    @Test
    void reset_ShouldReturnServerError_WhenExceptionOccurs() throws Exception {
        String errorMessage = "Database error";
        doThrow(new RuntimeException(errorMessage)).when(portfolioFacade).reset();

        mockMvc.perform(post("/portfolio/reset"))
                .andExpectAll(
                        status().isInternalServerError(),
                        content().string(errorMessage)
                );
    }
}