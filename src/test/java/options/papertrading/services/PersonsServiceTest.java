package options.papertrading.services;

import options.papertrading.dto.person.PersonDto;
import options.papertrading.models.person.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.security.PersonDetails;
import options.papertrading.util.converters.PersonConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonsServiceTest {

    @Mock
    private PersonsRepository personsRepository;

    @Mock
    private PersonConverter personConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private PersonsService personsService;

    @Test
    void findAll_ShouldReturnAllPersons() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.findAll()).thenReturn(List.of(mockPerson));

        List<Person> result = personsService.findAll();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockPerson, result.get(0)),
                () -> verify(personsRepository).findAll()
        );
    }

    @Test
    void findByEmail_ShouldReturnPerson_WhenExists() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockPerson));

        Optional<Person> result = personsService.findByEmail("test@example.com");

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(mockPerson, result.get()),
                () -> verify(personsRepository).findByEmail("test@example.com")
        );
    }

    @Test
    void create_ShouldSavePerson() {
        PersonDto mockDto = mock(PersonDto.class);
        Person mockPerson = mock(Person.class);
        when(personConverter.convertToPerson(mockDto)).thenReturn(mockPerson);

        personsService.create(mockDto);

        verify(personsRepository).save(mockPerson);
    }

    @Test
    void save_ShouldSaveAndReturnPerson() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.save(mockPerson)).thenReturn(mockPerson);

        Person result = personsService.save(mockPerson);

        assertAll(
                () -> assertEquals(mockPerson, result),
                () -> verify(personsRepository).save(mockPerson)
        );
    }

    @Test
    void getCurrentPerson_ShouldReturnAuthenticatedPerson() {
        Person mockPerson = mock(Person.class);
        PersonDetails mockDetails = mock(PersonDetails.class);
        Authentication mockAuth = mock(Authentication.class);
        SecurityContext mockContext = mock(SecurityContext.class);

        when(mockContext.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockDetails);
        when(mockDetails.getPerson()).thenReturn(mockPerson);
        when(mockPerson.getId()).thenReturn(1L);
        when(personsRepository.findById(1L)).thenReturn(mockPerson);
        SecurityContextHolder.setContext(mockContext);

        Person result = personsService.getCurrentPerson();

        assertAll(
                () -> assertEquals(mockPerson, result),
                () -> verify(personsRepository).findById(1L)
        );
    }

    @Test
    void getPersonById_ShouldReturnPerson() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.findById(1L)).thenReturn(mockPerson);

        Person result = personsService.getPersonById(1L);

        assertEquals(mockPerson, result);
    }

    @Test
    void getPersons_ShouldReturnConvertedPersons() {
        Person mockPerson = mock(Person.class);
        PersonDto mockDto = mock(PersonDto.class);
        PersonsService mockService = mock(PersonsService.class);

        when(beanFactory.getBean(PersonsService.class)).thenReturn(mockService);
        when(mockService.findAll()).thenReturn(List.of(mockPerson));
        when(personConverter.convertToPersonDto(mockPerson)).thenReturn(mockDto);

        List<PersonDto> result = personsService.getPersons();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(mockDto, result.get(0))
        );
    }

    @Test
    void updateActivationCode_ShouldUpdateCode() {
        String testEmail = "test@example.com";
        String activationCode = UUID.randomUUID().toString();

        Person mockPerson = mock(Person.class);
        when(mockPerson.getActivationCode()).thenReturn(activationCode);

        PersonsService mockService = mock(PersonsService.class);
        when(beanFactory.getBean(PersonsService.class)).thenReturn(mockService);
        when(mockService.findByEmail(testEmail)).thenReturn(Optional.of(mockPerson));

        doNothing().when(personsRepository).updateActivationCodeByEmail(testEmail, activationCode);

        Person result = personsService.updateActivationCode(testEmail);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(mockPerson, result),
                () -> verify(personsRepository).updateActivationCodeByEmail(testEmail, activationCode)
        );
    }

    @Test
    void updateActivationCode_ShouldThrow_WhenPersonNotFound() {
        PersonsService mockService = mock(PersonsService.class);
        when(beanFactory.getBean(PersonsService.class)).thenReturn(mockService);
        when(mockService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class,
                () -> personsService.updateActivationCode("test@example.com"));
    }

    @Test
    void findByActivationCode_ShouldReturnPerson() {
        Person mockPerson = mock(Person.class);
        when(personsRepository.findByActivationCode("code123")).thenReturn(mockPerson);

        Person result = personsService.findByActivationCode("code123");

        assertEquals(mockPerson, result);
    }

    @Test
    void updatePassword_ShouldEncodeAndSave() {
        Person mockPerson = mock(Person.class);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedPass");

        personsService.updatePassword(mockPerson, "newPass");

        assertAll(
                () -> verify(mockPerson).setPassword("encodedPass"),
                () -> verify(mockPerson).setActivationCode(null),
                () -> verify(personsRepository).save(mockPerson)
        );
    }

    @Test
    void class_ShouldHaveServiceAnnotation() {
        Service annotation = AnnotationUtils.findAnnotation(
                PersonsService.class, Service.class);
        assertNotNull(annotation);
    }

    @Test
    void findAll_ShouldBeTransactionalReadOnly() throws NoSuchMethodException {
        Method method = PersonsService.class.getMethod("findAll");
        Transactional transactional = AnnotationUtils.findAnnotation(method, Transactional.class);
        assertTrue(transactional.readOnly());
    }

    @Test
    void findByEmail_ShouldThrow_WhenEmailIsNull() {
        assertThrows(NullPointerException.class, () -> personsService.findByEmail(null));
    }

    @Test
    void create_ShouldThrow_WhenDtoIsNull() {
        assertThrows(NullPointerException.class, () -> personsService.create(null));
    }

    @Test
    void save_ShouldThrow_WhenPersonIsNull() {
        assertThrows(NullPointerException.class, () -> personsService.save(null));
    }
}