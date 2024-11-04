package options.papertrading.controllers;

import options.papertrading.facade.JournalFacade;
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
import org.springframework.ui.Model;

import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {JournalController.class, JournalFacade.class})
@ExtendWith(SpringExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class JournalControllerDiffblueTest {
    @MockBean
    private IJournalFacade iJournalFacade;

    @Autowired
    private JournalController journalController;

    /**
     * Method under test: {@link JournalController#showJournal(Model)}
     */
    @Test
    void testShowJournal() throws Exception {
        // Arrange
        when(iJournalFacade.showJournal()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portfolio/journal");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("journalList"))
                .andExpect(MockMvcResultMatchers.view().name("journal/journal"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("journal/journal"));
    }

    /**
     * Method under test: {@link JournalController#showJournal(Model)}
     */
    @Test
    void testShowJournal2() throws Exception {
        // Arrange
        when(iJournalFacade.showJournal()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/portfolio/journal");
        requestBuilder.characterEncoding("Encoding");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("journalList"))
                .andExpect(MockMvcResultMatchers.view().name("journal/journal"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("journal/journal"));
    }

    /**
     * Method under test: {@link JournalController#clearJournal()}
     */
    @Test
    void testClearJournal() throws Exception {
        // Arrange
        doNothing().when(iJournalFacade).clearJournal();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/portfolio/journal/clear");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?? ???????? ??????"));
    }

    /**
     * Method under test: {@link JournalController#clearJournal()}
     */
    @Test
    void testClearJournal2() throws Exception {
        // Arrange
        doNothing().when(iJournalFacade).clearJournal();
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(journalController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test: {@link JournalController#clearJournal()}
     */
    @Test
    void testClearJournal3() throws Exception {
        // Arrange
        doNothing().when(iJournalFacade).clearJournal();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/portfolio/journal/clear");
        requestBuilder.characterEncoding("Encoding");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(journalController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?? ???????? ??????"));
    }
}
