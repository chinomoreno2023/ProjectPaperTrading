package options.papertrading.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class MaintenanceControllerTest {

    private final MaintenanceController controller = new MaintenanceController();
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertTrue(MaintenanceController.class.isAnnotationPresent(Controller.class));
        assertTrue(MaintenanceController.class.isAnnotationPresent(RequestMapping.class));
        assertEquals("/maintenance",
                MaintenanceController.class.getAnnotation(RequestMapping.class).value()[0]);
    }

    @Test
    void showMaintenancePage_ShouldReturnCorrectView() throws Exception {
        mockMvc.perform(get("/maintenance"))
                .andExpectAll(
                        status().isOk(),
                        view().name("maintenance/maintenance")
                );
    }

    @Test
    void showMaintenancePageMethod_ShouldHaveGetMappingAnnotation() throws NoSuchMethodException {
        var method = MaintenanceController.class.getMethod("showMaintenancePage");
        assertTrue(method.isAnnotationPresent(GetMapping.class));
    }

    @Test
    void showMaintenancePage_ShouldRespondToRootPath() throws Exception {
        mockMvc.perform(get("/maintenance/"))
                .andExpect(status().isOk());
    }
}