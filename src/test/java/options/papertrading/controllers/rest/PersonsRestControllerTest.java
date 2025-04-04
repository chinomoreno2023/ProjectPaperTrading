package options.papertrading.controllers.rest;

import jakarta.validation.Valid;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.CharacterEncodingFilter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PersonsRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPersonFacade jsonPersonFacade;

    @InjectMocks
    private PersonsRestController personsRestController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(personsRestController)
                .addFilter(new CharacterEncodingFilter("UTF-8", true, true))
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .alwaysDo(result -> {
                    String content = new String(
                            result.getResponse().getContentAsByteArray(),
                            StandardCharsets.UTF_8
                    );
                    System.out.println("Response (UTF-8): " + content);
                })
                .build();
    }

    @Test
    void classAnnotations_ShouldBeCorrect() {
        assertAll(
                () -> assertTrue(PersonsRestController.class.isAnnotationPresent(RestController.class)),
                () -> assertTrue(PersonsRestController.class.isAnnotationPresent(RequestMapping.class)),
                () -> assertEquals("/persons", PersonsRestController.class.getAnnotation(RequestMapping.class).value()[0])
        );
    }

    @Test
    void getPersons_ShouldReturnNoContent_WhenEmpty() throws Exception {
        when(jsonPersonFacade.getPersons()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/persons"))
                .andExpectAll(
                        status().isNoContent(),
                        content().string("")
                );
    }

    @Test
    void getPersons_ShouldReturnOk_WithPersons() throws Exception {
        List<PersonDto> persons = List.of(new PersonDto());
        when(jsonPersonFacade.getPersons()).thenReturn(persons);

        mockMvc.perform(get("/persons"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"),
                        jsonPath("$").isArray(),
                        jsonPath("$.length()").value(1)
                );
    }

    @Test
    void getPersonsMethod_ShouldHaveGetMappingAnnotation() throws NoSuchMethodException {
        var method = PersonsRestController.class.getMethod("getPersons");
        assertTrue(method.isAnnotationPresent(GetMapping.class));
    }

    @Test
    void create_ShouldReturnOk_WhenValidRequest() throws Exception {
        String validPersonJson = """
    {
        "email": "test@example.com",
        "username": "validuser"
    }
    """;

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPersonJson))
                .andExpectAll(
                        status().isOk(),
                        content().string("The user has been added to the database")
                );

        verify(jsonPersonFacade).create(argThat(person ->
                person.getEmail().equals("test@example.com") &&
                        person.getUsername().equals("validuser")
        ));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"),
                        jsonPath("$.message").exists(),
                        jsonPath("$.timestamp").exists()
                );

        verify(jsonPersonFacade, never()).create(any());
    }

    @Test
    void create_ShouldReturnServerError_WhenFacadeFails() throws Exception {
        String validPersonJson = """
        {
            "email": "valid@example.com",
            "username": "validuser"
        }
        """;

        doThrow(HttpServerErrorException.class)
                .when(jsonPersonFacade)
                .create(any(PersonDto.class));

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPersonJson))
                .andExpectAll(
                        status().isInternalServerError(),
                        content().string("Server error adding new user")
                );
    }

    @Test
    void createMethod_ShouldHaveCorrectAnnotations() throws NoSuchMethodException {
        var method = PersonsRestController.class.getMethod("create", PersonDto.class, BindingResult.class);

        assertAll(
                () -> assertTrue(method.isAnnotationPresent(PostMapping.class)),
                () -> assertTrue(method.getParameters()[0].isAnnotationPresent(Valid.class)),
                () -> assertTrue(method.getParameters()[0].isAnnotationPresent(RequestBody.class))
        );
    }

    @Test
    void handleException_ShouldReturnBadRequest() throws Exception {
        PersonsRestController controller = new PersonsRestController(jsonPersonFacade);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Пустой JSON вызовет ошибки валидации
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value(containsString("Имя не может быть пустым")),
                        jsonPath("$.message").value(containsString("Вы забыли ввести почту")),
                        jsonPath("$.timestamp").exists()
                );
    }
}