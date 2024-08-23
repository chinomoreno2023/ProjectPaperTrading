package options.papertrading.controllers;

import options.papertrading.facade.AuthFacade;
import options.papertrading.facade.PersonFacade;
import options.papertrading.facade.interfaces.IAuthFacade;
import options.papertrading.facade.interfaces.IPersonFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.validators.TestYourIq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.ArrayList;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {AuthController.class, AuthFacade.class, PersonFacade.class})
@ExtendWith(SpringExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class AuthControllerDiffblueTest {
    @Autowired
    private AuthController authController;

    @MockBean
    private IAuthFacade iAuthFacade;

    @MockBean
    private IPersonFacade iPersonFacade;

    /**
     * Method under test: {@link AuthController#activate(String)}
     */
    @Test
    void testActivate() throws Exception {
        // Arrange
        when(iAuthFacade.activatePerson(Mockito.<String>any())).thenReturn(true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/activate/{code}", "Code");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("auth/activation"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/activation"));
    }

    /**
     * Method under test: {@link AuthController#testYourIq(Model)}
     */
    @Test
    void testTestYourIq2() throws Exception {
        // Arrange
        when(iAuthFacade.testYourIq()).thenReturn(new TestYourIq(10, 10, 1));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/hello");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("testYourIq"))
                .andExpect(MockMvcResultMatchers.view().name("auth/hello"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/hello"));
    }

    /**
     * Method under test: {@link AuthController#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile")
                .param("password", "foo")
                .param("retypedPassword", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?????? ??????? ???????"));
    }

    /**
     * Method under test: {@link AuthController#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword2() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile")
                .param("password", "https://example.org/example")
                .param("retypedPassword", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?????? ?? ?????????"));
    }

    /**
     * Method under test: {@link AuthController#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword3() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile")
                .param("password", "")
                .param("retypedPassword", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???? ?? ????? ???? ???????"));
    }

    /**
     * Method under test: {@link AuthController#updatePassword(String, String)}
     */
    @Test
    void testUpdatePassword4() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile")
                .param("password", "foo")
                .param("retypedPassword", "");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???? ?? ????? ???? ???????"));
    }

    /**
     * Method under test: {@link AuthController#activate(String)}
     */
    @Test
    void testActivate2() throws Exception {
        // Arrange
        when(iAuthFacade.activatePerson(Mockito.<String>any())).thenReturn(false);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/activate/{code}", "Code");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("auth/wrong_code"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/wrong_code"));
    }

    /**
     * Method under test: {@link AuthController#activate(String)}
     */
    @Test
    void testActivate3() throws Exception {
        // Arrange
        when(iAuthFacade.activatePerson(Mockito.<String>any())).thenReturn(true);
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test: {@link AuthController#checkMailForRestorePassword(String)}
     */
    @Test
    void testCheckMailForRestorePassword() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.updateActivationCode(Mockito.<String>any())).thenReturn(person);
        doNothing().when(iPersonFacade).sendMailForResetPassword(Mockito.<Person>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/restore")
                .param("emailForRestore", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???????? ??????????? ? ??????"));
    }

    /**
     * Method under test: {@link AuthController#loginPage()}
     */
    @Test
    void testLoginPage() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/login");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("auth/login"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/login"));
    }

    /**
     * Method under test: {@link AuthController#loginPage()}
     */
    @Test
    void testLoginPage2() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/login");
        requestBuilder.characterEncoding("Encoding");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("auth/login"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/login"));
    }

    /**
     * Method under test: {@link AuthController#openUserPage(Model)}
     */
    @Test
    void testOpenUserPage() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/profile");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("person"))
                .andExpect(MockMvcResultMatchers.view().name("auth/profile"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/profile"));
    }

    /**
     * Method under test:
     * {@link AuthController#performRegistration(Person, BindingResult, Model)}
     */
    @Test
    void testPerformRegistration() throws Exception {
        // Arrange
        doNothing().when(iAuthFacade).validate(Mockito.<Person>any(), Mockito.<Errors>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/registration");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("person"))
                .andExpect(MockMvcResultMatchers.view().name("auth/registration"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/registration"));
    }

    /**
     * Method under test:
     * {@link AuthController#requestForRestorePassword(String, Model)}
     */
    @Test
    void testRequestForRestorePassword() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/auth/restore/{code}", "Code");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.model().attributeExists("code"))
                .andExpect(MockMvcResultMatchers.view().name("auth/restorePassword"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/restorePassword"));
    }

    /**
     * Method under test:
     * {@link AuthController#restorePassword(String, String, String)}
     */
    @Test
    void testRestorePassword() throws Exception {
        // Arrange
        when(iAuthFacade.validate(Mockito.<String>any())).thenReturn(true);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/restore/confirmation")
                .param("code", "foo")
                .param("password", "foo")
                .param("retypedPassword", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?????? ??????? ???????"));
    }

    /**
     * Method under test:
     * {@link AuthController#restorePassword(String, String, String)}
     */
    @Test
    void testRestorePassword2() throws Exception {
        // Arrange
        when(iAuthFacade.validate(Mockito.<String>any())).thenReturn(false);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/restore/confirmation")
                .param("code", "foo")
                .param("password", "foo")
                .param("retypedPassword", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("?????? ?????? ???? ?? ????? ?????? ???????? ? ???? ?? ????? ??????"));
    }

    /**
     * Method under test:
     * {@link AuthController#restorePassword(String, String, String)}
     */
    @Test
    void testRestorePassword3() throws Exception {
        // Arrange
        when(iAuthFacade.validate(Mockito.<String>any())).thenReturn(true);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/restore/confirmation")
                .param("code", "foo")
                .param("password", "https://example.org/example")
                .param("retypedPassword", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("?????? ?? ?????????"));
    }

    /**
     * Method under test:
     * {@link AuthController#restorePassword(String, String, String)}
     */
    @Test
    void testRestorePassword4() throws Exception {
        // Arrange
        when(iAuthFacade.validate(Mockito.<String>any())).thenReturn(true);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/restore/confirmation")
                .param("code", "foo")
                .param("password", "")
                .param("retypedPassword", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???? ?? ????? ???? ???????"));
    }

    /**
     * Method under test:
     * {@link AuthController#restorePassword(String, String, String)}
     */
    @Test
    void testRestorePassword5() throws Exception {
        // Arrange
        when(iAuthFacade.validate(Mockito.<String>any())).thenReturn(true);

        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        when(iPersonFacade.findByActivationCode(Mockito.<String>any())).thenReturn(person);
        doNothing().when(iPersonFacade).updatePassword(Mockito.<Person>any(), Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/restore/confirmation")
                .param("code", "foo")
                .param("password", "foo")
                .param("retypedPassword", "");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???? ?? ????? ???? ???????"));
    }

    /**
     * Method under test: {@link AuthController#sendFeedback(String, String)}
     */
    @Test
    void testSendFeedback() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        doNothing().when(iPersonFacade).sendMail(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile/feedback")
                .param("message", "foo")
                .param("subject", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("??????????"));
    }

    /**
     * Method under test: {@link AuthController#sendFeedback(String, String)}
     */
    @Test
    void testSendFeedback2() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        doNothing().when(iPersonFacade).sendMail(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile/feedback")
                .param("message", "")
                .param("subject", "foo");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???? ?? ????? ???? ???????"));
    }

    /**
     * Method under test: {@link AuthController#sendFeedback(String, String)}
     */
    @Test
    void testSendFeedback3() throws Exception {
        // Arrange
        Person person = new Person();
        person.setActivationCode("Activation Code");
        person.setActive(true);
        person.setCurrentNetPosition(10.0d);
        person.setEmail("jane.doe@example.org");
        person.setId(1L);
        person.setJournal(new ArrayList<>());
        person.setOpenLimit(10.0d);
        person.setOptionsInPortfolio(new ArrayList<>());
        person.setPassword("iloveyou");
        person.setRole("Role");
        person.setUsername("janedoe");
        doNothing().when(iPersonFacade).sendMail(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any());
        when(iPersonFacade.getCurrentPerson()).thenReturn(person);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/profile/feedback")
                .param("message", "foo")
                .param("subject", "");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(authController).build().perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???? ?? ????? ???? ???????"));
    }

    /**
     * Method under test:
     * {@link AuthController#testYourIq(TestYourIq, BindingResult, Person)}
     */
    @Test
    void testTestYourIq() throws Exception {
        // Arrange
        doNothing().when(iAuthFacade).testYourIqValidate(Mockito.<TestYourIq>any(), Mockito.<BindingResult>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/hello");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(2))
                .andExpect(MockMvcResultMatchers.model().attributeExists("person"))
                .andExpect(MockMvcResultMatchers.view().name("auth/hello"))
                .andExpect(MockMvcResultMatchers.forwardedUrl("auth/hello"));
    }
}
