package options.papertrading.controllers.rest;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.OptionFacade;
import options.papertrading.facade.PortfolioFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PortfolioRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortfolioFacade portfolioFacade;

    @Mock
    private OptionFacade optionFacade;

    @InjectMocks
    private PortfolioRestController portfolioRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(portfolioRestController)
                .build();
    }

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertTrue(PortfolioRestController.class.isAnnotationPresent(RestController.class));
        assertTrue(PortfolioRestController.class.isAnnotationPresent(RequestMapping.class));
        assertEquals("/rest/portfolio",
                PortfolioRestController.class.getAnnotation(RequestMapping.class).value()[0]);
    }

    @Test
    void showAllPortfolios_ShouldReturnNoContent_WhenEmpty() throws Exception {
        when(portfolioFacade.showAllPortfolios()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/rest/portfolio"))
                .andExpectAll(
                        status().isNoContent(),
                        content().string("")
                );
    }

    @Test
    void showAllPortfolios_ShouldReturnPortfolios_WhenExist() throws Exception {
        List<PortfolioDto> portfolios = List.of(new PortfolioDto());
        when(portfolioFacade.showAllPortfolios()).thenReturn(portfolios);

        mockMvc.perform(get("/rest/portfolio"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(1)
                );
    }

    @Test
    void showOptionsList_ShouldReturnNoContent_WhenEmpty() throws Exception {
        when(optionFacade.showOptionsList()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/rest/portfolio/options"))
                .andExpectAll(
                        status().isNoContent(),
                        content().string("")
                );
    }

    @Test
    void showOptionsList_ShouldReturnOptions_WhenExist() throws Exception {
        List<OptionDto> options = List.of(new OptionDto());
        when(optionFacade.showOptionsList()).thenReturn(options);

        mockMvc.perform(get("/rest/portfolio/options"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(1)
                );
    }

    @Test
    void addPortfolio_ShouldReturnOk_WhenValidRequest() throws Exception {
        String validOptionJson = "{\"id\":\"OPT123\",\"volume\":10}";

        mockMvc.perform(post("/rest/portfolio/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOptionJson))
                .andExpectAll(
                        status().isOk(),
                        content().string("Option added to portfolio")
                );

        verify(portfolioFacade).addPortfolio(any(OptionDto.class));
    }

    @Test
    void addPortfolio_ShouldReturnBadRequest_WhenCustomValidationFails() throws Exception {
        String json = "{\"id\":\"OPT123\",\"volume\":10}";

        doAnswer(invocation -> {
            BindingResult result = invocation.getArgument(1);
            result.rejectValue("volume", "error.volume", "Custom volume error");
            return null;
        }).when(optionFacade).volumeValidate(any(), any());

        mockMvc.perform(post("/rest/portfolio/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("Custom volume error")
                );
    }

    @Test
    void addPortfolio_ShouldReturnServerError_WhenFacadeFails() throws Exception {
        String validOptionJson = "{\"id\":\"OPT123\",\"volume\":10}";
        doThrow(HttpServerErrorException.class).when(portfolioFacade).addPortfolio(any());

        mockMvc.perform(post("/rest/portfolio/options")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOptionJson))
                .andExpectAll(
                        status().isInternalServerError(),
                        content().string("Server error adding option to portfolio")
                );
    }

    @Test
    void addPortfolioMethod_ShouldHaveCorrectAnnotations() throws NoSuchMethodException {
        var method = PortfolioRestController.class.getMethod(
                "addPortfolio",
                OptionDto.class,
                BindingResult.class
        );

        assertAll(
                () -> assertTrue(method.isAnnotationPresent(PostMapping.class)),
                () -> assertEquals("/options", method.getAnnotation(PostMapping.class).value()[0]),
                () -> assertTrue(method.isAnnotationPresent(Transactional.class)),
                () -> assertTrue(method.getParameters()[0].isAnnotationPresent(RequestBody.class))
        );
    }
}