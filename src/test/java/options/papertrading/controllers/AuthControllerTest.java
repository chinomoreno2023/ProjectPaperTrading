package options.papertrading.controllers;

import options.papertrading.facade.AuthFacade;
import options.papertrading.facade.PersonFacade;
import options.papertrading.models.TestYourIq;
import options.papertrading.models.person.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthFacade authFacade;

    @Mock
    private PersonFacade personFacade;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void loginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void performRegistration_ShouldReturnRegistrationView_WhenValidationFails() throws Exception {
        mockMvc.perform(post("/auth/registration")
                        .flashAttr("person", new Person())
                        .flashAttr("org.springframework.validation.BindingResult.person", bindingResult))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"));
    }

    @Test
    void performRegistration_ShouldReturnConfirmationView_WhenSuccess() throws Exception {
        doNothing().when(authFacade).register(any(Person.class));

        Person validPerson = new Person();
        validPerson.setEmail("test@example.com");
        validPerson.setUsername("testuser");
        validPerson.setPassword("ValidPass123");

        mockMvc.perform(post("/auth/registration")
                        .flashAttr("person", validPerson)
                        .flashAttr("org.springframework.validation.BindingResult.person", bindingResult))
                .andExpectAll(
                        status().isOk(),
                        view().name("auth/confirmation")
                );

        verify(authFacade).register(any(Person.class));
    }

    @Test
    void activate_ShouldReturnActivationView_WhenCodeValid() throws Exception {
        when(authFacade.activatePerson(anyString())).thenReturn(true);

        mockMvc.perform(get("/auth/activate/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/activation"));
    }

    @Test
    void activate_ShouldReturnWrongCodeView_WhenCodeInvalid() throws Exception {
        when(authFacade.activatePerson(anyString())).thenReturn(false);

        mockMvc.perform(get("/auth/activate/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/wrong_code"));
    }

    @Test
    void testYourIq_ShouldReturnHelloViewWithModel() throws Exception {
        TestYourIq test = new TestYourIq(1, 2, 3);
        when(authFacade.testYourIq()).thenReturn(test);

        mockMvc.perform(get("/auth/hello"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/hello"))
                .andExpect(model().attributeExists("testYourIq"));
    }

    @Test
    void testYourIqPost_ShouldReturnHelloView_WhenValidationFails() throws Exception {
        TestYourIq testYourIq = new TestYourIq(1, 2, 3);

        doAnswer(invocation -> {
            BindingResult bindingResult = invocation.getArgument(1);
            bindingResult.rejectValue("result", "error.code", "Неверный результат");
            return null;
        }).when(authFacade).testYourIqValidate(any(TestYourIq.class), any(BindingResult.class));

        mockMvc.perform(post("/auth/hello")
                        .flashAttr("testYourIq", testYourIq)
                        .flashAttr("person", new Person())) // Добавляем обязательный атрибут
                .andExpectAll(
                        status().isOk(),
                        view().name("auth/hello"),
                        model().attributeExists("testYourIq"),
                        model().attributeExists("person")
                );
    }

    @Test
    void checkMailForRestorePassword_ShouldReturnOk_WhenEmailValid() throws Exception {
        Person person = new Person();
        when(personFacade.updateActivationCode(anyString())).thenReturn(person);

        mockMvc.perform(post("/auth/restore")
                        .param("emailForRestore", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Следуйте инструкциям в письме"));

        verify(personFacade).sendMailForResetPassword(any(Person.class));
    }

    @Test
    void checkMailForRestorePassword_ShouldReturnBadRequest_WhenEmailInvalid() throws Exception {
        when(personFacade.updateActivationCode(anyString())).thenThrow(NullPointerException.class);

        mockMvc.perform(post("/auth/restore")
                        .param("emailForRestore", "invalid@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Пользователь с такой почтой не найден"));
    }


    @Test
    void restorePasswordPage_ShouldReturnRestoreView_WhenCodeValid() throws Exception {
        when(personFacade.findByActivationCode(anyString())).thenReturn(new Person());

        mockMvc.perform(get("/auth/restore/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/restorePassword"))
                .andExpect(model().attributeExists("code"));
    }

    @Test
    void restorePasswordConfirmation_ShouldReturnOk_WhenPasswordsMatch() throws Exception {
        Person person = new Person();
        when(personFacade.findByActivationCode(anyString())).thenReturn(person);
        when(authFacade.validate(anyString())).thenReturn(true);

        mockMvc.perform(post("/auth/restore/confirmation")
                        .param("password", "ValidPass123")
                        .param("retypedPassword", "ValidPass123")
                        .param("code", "123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пароль успешно изменен"));

        verify(personFacade).updatePassword(any(Person.class), anyString());
    }

    @Test
    void updatePassword_ShouldReturnOk_WhenPasswordsMatch() throws Exception {
        when(personFacade.getCurrentPerson()).thenReturn(new Person());

        mockMvc.perform(post("/auth/profile")
                        .param("password", "newPass123")
                        .param("retypedPassword", "newPass123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Пароль успешно изменен"));
    }

    @Test
    void sendFeedback_ShouldReturnOk_WhenMailSent() throws Exception {
        ReflectionTestUtils.setField(authController, "myMail", "test@example.com");
        Person mockPerson = new Person();
        mockPerson.setEmail("test@example.com");

        when(personFacade.getCurrentPerson()).thenReturn(mockPerson);

        doNothing().when(personFacade).sendMail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/auth/profile/feedback")
                        .param("subject", "Test")
                        .param("message", "Test message")
                        .contentType("text/plain;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string("Отправлено"));

        verify(personFacade).sendMail(eq("test@example.com"), eq("Test"),
                eq("Test message<br><br>Почта пользователя:<br>test@example.com"));
    }
}