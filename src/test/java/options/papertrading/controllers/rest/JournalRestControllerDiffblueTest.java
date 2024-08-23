package options.papertrading.controllers.rest;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {JournalRestController.class})
@ExtendWith(SpringExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class JournalRestControllerDiffblueTest {
    @MockBean
    private IJournalFacade iJournalFacade;

    @Autowired
    private JournalRestController journalRestController;

    /**
     * Method under test: {@link JournalRestController#showJournal()}
     */
    @Test
    void testShowJournal() throws Exception {
        // Arrange
        when(iJournalFacade.showJournal()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/journal");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(journalRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link JournalRestController#showJournal()}
     */
    @Test
    void testShowJournal2() throws Exception {
        // Arrange
        JournalDto journalDto = new JournalDto();
        journalDto.setBuyOrWrite(19088743);
        journalDto.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journalDto.setOptionName("?");
        journalDto.setPrice(10.0d);
        journalDto.setVolume(1);

        ArrayList<JournalDto> journalDtoList = new ArrayList<>();
        journalDtoList.add(journalDto);
        when(iJournalFacade.showJournal()).thenReturn(journalDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/journal");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "[{\"dateAndTime\":[1970,1,1,0,0],\"optionName\":\"?\",\"volume\":1,\"price\":10.0,\"buyOrWrite\":19088743}]"));
    }

    /**
     * Method under test: {@link JournalRestController#clearJournal()}
     */
    @Test
    void testClearJournal() throws Exception {
        // Arrange
        doNothing().when(iJournalFacade).clearJournal();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/journal/clear");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Journal cleared successfully"));
    }

    /**
     * Method under test: {@link JournalRestController#clearJournal()}
     */
    @Test
    void testClearJournal2() throws Exception {
        // Arrange
        doNothing().when(iJournalFacade).clearJournal();
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(journalRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test: {@link JournalRestController#clearJournal()}
     */
    @Test
    void testClearJournal3() throws Exception {
        // Arrange
        doNothing().when(iJournalFacade).clearJournal();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/journal/clear");
        requestBuilder.characterEncoding("Encoding");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Journal cleared successfully"));
    }
}
