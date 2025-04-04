package options.papertrading.controllers;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.JournalFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JournalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JournalFacade journalFacade;

    @InjectMocks
    private JournalController journalController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(journalController).build();
    }

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertTrue(JournalController.class.isAnnotationPresent(Controller.class));
        assertTrue(JournalController.class.isAnnotationPresent(RequestMapping.class));
        assertEquals("/portfolio/journal",
                JournalController.class.getAnnotation(RequestMapping.class).value()[0]);
    }

    @Test
    void showJournal_ShouldReturnJournalView_WithModelAttributes() throws Exception {
        List<JournalDto> journals = List.of(new JournalDto());
        when(journalFacade.showJournal()).thenReturn(journals);

        mockMvc.perform(get("/portfolio/journal"))
                .andExpectAll(
                        status().isOk(),
                        view().name("journal/journal"),
                        model().attributeExists("journalList"),
                        model().attribute("journalList", journals)
                );

        verify(journalFacade).showJournal();
    }

    @Test
    void showJournal_ShouldReturnEmptyModel_WhenNoData() throws Exception {
        when(journalFacade.showJournal()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/portfolio/journal"))
                .andExpectAll(
                        status().isOk(),
                        model().attribute("journalList", Collections.emptyList())
                );
    }

    @Test
    void clearJournal_ShouldReturnOk_WhenSuccess() throws Exception {
        mockMvc.perform(post("/portfolio/journal/clear"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN),
                        content().string("Вы очистили журнал")
                );

        verify(journalFacade).clearJournal();
    }

    @Test
    void clearJournal_ShouldReturnServerError_WhenExceptionOccurs() throws Exception {
        String errorMessage = "Database error";
        doThrow(new RuntimeException(errorMessage)).when(journalFacade).clearJournal();

        mockMvc.perform(post("/portfolio/journal/clear"))
                .andExpectAll(
                        status().isInternalServerError(),
                        content().string(errorMessage)
                );
    }

    @Test
    void showJournalMethod_ShouldHaveGetMappingAnnotation() throws NoSuchMethodException {
        var method = JournalController.class.getMethod("showJournal", Model.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class));
    }

    @Test
    void clearJournalMethod_ShouldHavePostMappingAnnotation() throws NoSuchMethodException {
        var method = JournalController.class.getMethod("clearJournal");
        assertTrue(method.isAnnotationPresent(PostMapping.class));
        assertEquals("/clear", method.getAnnotation(PostMapping.class).value()[0]);
    }
}