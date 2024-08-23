package options.papertrading.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacade;
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

@ContextConfiguration(classes = {PersonsRestController.class})
@ExtendWith(SpringExtension.class)
@Execution(ExecutionMode.SAME_THREAD)
class PersonsRestControllerDiffblueTest {
    @MockBean
    private IPersonFacade iPersonFacade;

    @Autowired
    private PersonsRestController personsRestController;

    /**
     * Method under test: {@link PersonsRestController#getPersons()}
     */
    @Test
    void testGetPersons() throws Exception {
        // Arrange
        when(iPersonFacade.getPersons()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/persons");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(personsRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link PersonsRestController#getPersons()}
     */
    @Test
    void testGetPersons2() throws Exception {
        // Arrange
        PersonDto personDto = new PersonDto();
        personDto.setEmail("jane.doe@example.org");
        personDto.setRole("?");
        personDto.setUsername("janedoe");

        ArrayList<PersonDto> personDtoList = new ArrayList<>();
        personDtoList.add(personDto);
        when(iPersonFacade.getPersons()).thenReturn(personDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/persons");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(personsRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("[{\"username\":\"janedoe\",\"email\":\"jane.doe@example.org\",\"role\":\"?\"}]"));
    }

    /**
     * Method under test: {@link PersonsRestController#getPersons()}
     */
    @Test
    void testGetPersons3() throws Exception {
        // Arrange
        when(iPersonFacade.getPersons()).thenReturn(new ArrayList<>());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(personsRestController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Method under test:
     * {@link PersonsRestController#create(PersonDto, BindingResult)}
     */
    @Test
    void testCreate() throws Exception {
        // Arrange
        doNothing().when(iPersonFacade).create(Mockito.<PersonDto>any());

        PersonDto personDto = new PersonDto();
        personDto.setEmail("jane.doe@example.org");
        personDto.setRole("Role");
        personDto.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(personDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(personsRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("???????????? ???????? ? ???? ??????"));
    }
}
