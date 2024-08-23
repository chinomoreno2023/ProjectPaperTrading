package options.papertrading.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.OptionFacade;
import options.papertrading.facade.PortfolioFacade;
import options.papertrading.facade.interfaces.IOptionFacade;
import options.papertrading.facade.interfaces.IPortfolioFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {PortfolioRestController.class, PortfolioFacade.class, OptionFacade.class})
@ExtendWith(SpringExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class PortfolioRestControllerDiffblueTest {
    @MockBean
    private IOptionFacade iOptionFacade;

    @MockBean
    private IPortfolioFacade iPortfolioFacade;

    @Autowired
    private PortfolioRestController portfolioRestController;

    /**
     * Method under test: {@link PortfolioRestController#showAllPortfolios()}
     */
    @Test
    void testShowAllPortfolios() throws Exception {
        // Arrange
        when(iPortfolioFacade.showAllPortfolios()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/portfolio");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link PortfolioRestController#showAllPortfolios()}
     */
    @Test
    void testShowAllPortfolios2() throws Exception {
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
        portfolioDto.setType("?");
        portfolioDto.setVariatMargin(10.0d);
        portfolioDto.setVolatility(10.0d);
        portfolioDto.setVolatilityWhenWasTrade(10.0d);
        portfolioDto.setVolume(1);
        portfolioDto.setWriteCollateral(10.0d);

        ArrayList<PortfolioDto> portfolioDtoList = new ArrayList<>();
        portfolioDtoList.add(portfolioDto);
        when(iPortfolioFacade.showAllPortfolios()).thenReturn(portfolioDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/portfolio");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "[{\"id\":\"42\",\"strike\":1,\"type\":\"?\",\"volume\":1,\"daysToMaturity\":1,\"tradePrice\":10.0,\"price\":10.0,"
                                        + "\"volatilityWhenWasTrade\":10.0,\"volatility\":10.0,\"collateralWhenWasTrade\":10.0,\"buyCollateral\":10.0,"
                                        + "\"writeCollateral\":10.0,\"variatMargin\":10.0,\"currentNetPosition\":10.0,\"openLimit\":10.0,\"stepPrice\":10"
                                        + ".0}]"));
    }

    /**
     * Method under test: {@link PortfolioRestController#showAllPortfolios()}
     */
    @Test
    void testShowAllPortfolios3() throws Exception {
        // Arrange
        when(iPortfolioFacade.showAllPortfolios()).thenReturn(new ArrayList<>());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test: {@link PortfolioRestController#showOptionsList()}
     */
    @Test
    void testShowOptionsList() throws Exception {
        // Arrange
        when(iOptionFacade.showOptionsList()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/portfolio/options");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(portfolioRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link PortfolioRestController#showOptionsList()}
     */
    @Test
    void testShowOptionsList2() throws Exception {
        // Arrange
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyCollateral(10.0d);
        optionDto.setBuyOrWrite(19088743);
        optionDto.setDaysToMaturity(1);
        optionDto.setId("42");
        optionDto.setPrice(10.0d);
        optionDto.setStepPrice(10.0d);
        optionDto.setStrike(1);
        optionDto.setType("?");
        optionDto.setVolatility(10.0d);
        optionDto.setVolume(1);
        optionDto.setWriteCollateral(10.0d);

        ArrayList<OptionDto> optionDtoList = new ArrayList<>();
        optionDtoList.add(optionDto);
        when(iOptionFacade.showOptionsList()).thenReturn(optionDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/portfolio/options");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "[{\"id\":\"42\",\"strike\":1,\"type\":\"?\",\"price\":10.0,\"volume\":1,\"volatility\":10.0,\"daysToMaturity\":1,"
                                        + "\"buyCollateral\":10.0,\"writeCollateral\":10.0,\"buyOrWrite\":19088743,\"stepPrice\":10.0}]"));
    }

    /**
     * Method under test:
     * {@link PortfolioRestController#addPortfolio(OptionDto, BindingResult)}
     */
    @Test
    void testAddPortfolio() throws Exception {
        // Arrange
        doNothing().when(iPortfolioFacade).addPortfolio(Mockito.<OptionDto>any());
        doNothing().when(iOptionFacade).volumeValidate(Mockito.<OptionDto>any(), Mockito.<BindingResult>any());

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
        String content = (new ObjectMapper()).writeValueAsString(optionDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/portfolio/options")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(portfolioRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?????? ???????? ? ????????"));
    }
}
