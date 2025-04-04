package options.papertrading.controllers.rest;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JournalRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IJournalFacade journalFacade;

    @InjectMocks
    private JournalRestController journalRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(journalRestController).build();
    }

    @Test
    void showJournal_ShouldReturnOk_WhenJournalsExist() throws Exception {
        List<JournalDto> journalDtos = List.of(new JournalDto());
        when(journalFacade.showJournal()).thenReturn(journalDtos);

        mockMvc.perform(get("/rest/journal"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void showJournal_ShouldReturnNoContent_WhenNoJournalsExist() throws Exception {
        when(journalFacade.showJournal()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/rest/journal"))
                .andExpectAll(status().isNoContent());
    }

    @Test
    void clearJournal_ShouldReturnOk_WhenClearingSuccessful() throws Exception {
        mockMvc.perform(post("/rest/journal/clear"))
                .andExpectAll(
                        status().isOk(),
                        content().string("Journal cleared successfully")
                );
    }

    @Test
    void clearJournal_ShouldReturnInternalServerError_WhenExceptionThrown() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(journalFacade).clearJournal();

        mockMvc.perform(post("/rest/journal/clear"))
                .andExpectAll(
                        status().isInternalServerError(),
                        content().string("Database error")
                );
    }

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertAll(
                () -> assertTrue(
                        JournalRestController.class.isAnnotationPresent(RestController.class),
                        "Missing @RestController"
                ),
                () -> assertEquals(
                        "rest/journal",
                        JournalRestController.class.getAnnotation(RequestMapping.class).value()[0],
                        "Incorrect base path"
                )
        );
    }

    @Test
    void showJournal_ShouldReturnOk_WhenDataExists() throws Exception {
        when(journalFacade.showJournal()).thenReturn(List.of(new JournalDto()));

        mockMvc.perform(get("/rest/journal"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()").value(1)
                );
    }

    @Test
    void showJournalMethod_ShouldHaveCorrectAnnotations() throws NoSuchMethodException {
        Method method = JournalRestController.class.getMethod("showJournal");

        assertTrue(method.isAnnotationPresent(GetMapping.class), "Missing @GetMapping");
    }
}
