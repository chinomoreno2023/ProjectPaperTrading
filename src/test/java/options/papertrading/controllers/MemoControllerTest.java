package options.papertrading.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class MemoControllerTest {

    private final MemoController controller = new MemoController();
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertAll(
                () -> assertTrue(MemoController.class.isAnnotationPresent(Controller.class)),
                () -> assertTrue(MemoController.class.isAnnotationPresent(RequestMapping.class)),
                () -> assertEquals("/portfolio/memo",
                        MemoController.class.getAnnotation(RequestMapping.class).value()[0])
        );
    }

    @Test
    void openMemoPage_ShouldReturnCorrectView() throws Exception {
        mockMvc.perform(get("/portfolio/memo"))
                .andExpectAll(
                        status().isOk(),
                        view().name("memo/memo")
                );
    }

    @Test
    void openMemoPageForRegistered_ShouldReturnCorrectView() throws Exception {
        mockMvc.perform(get("/portfolio/memo/memo-for-registered"))
                .andExpectAll(
                        status().isOk(),
                        view().name("memo/memoForRegistered")
                );
    }

    @Test
    void openMemoPageMethod_ShouldHaveGetMappingAnnotation() throws NoSuchMethodException {
        var method = MemoController.class.getMethod("openMemoPage");
        assertTrue(method.isAnnotationPresent(GetMapping.class));
    }

    @Test
    void openMemoPageForRegisteredMethod_ShouldHaveGetMappingAnnotation() throws NoSuchMethodException {
        var method = MemoController.class.getMethod("openMemoPageForRegistered");
        assertAll(
                () -> assertTrue(method.isAnnotationPresent(GetMapping.class)),
                () -> assertEquals("/memo-for-registered",
                        method.getAnnotation(GetMapping.class).value()[0])
        );
    }

    @Test
    void openMemoPage_ShouldLogAccess() throws Exception {
        mockMvc.perform(get("/portfolio/memo"));
    }
}