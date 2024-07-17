package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IPersonFacade;
import options.papertrading.util.exceptions.PersonErrorResponse;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/persons")
@AllArgsConstructor
public class PersonsRestController {
    private final IPersonFacade jsonPersonFacade;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getPersons() {
        final List<PersonDto> personDtoList = jsonPersonFacade.getPersons();
        log.info("Request to get list of all persons. Result: {}", personDtoList);
        return personDtoList.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(personDtoList);
    }

    @PostMapping
    public ResponseEntity<String> create(@Valid @RequestBody @NonNull PersonDto personDto,
                                                             @NonNull BindingResult bindingResult) {
        log.info("Trying to add new person: {}", personDto);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                            .append(" - ").append(error.getDefaultMessage())
                            .append(";");
            }
            log.info("Person was not created. Error: {}", errorMessage);
            throw new PersonNotCreatedException(errorMessage.toString());
        }

        try {
            jsonPersonFacade.create(personDto);
            return ResponseEntity.ok("Пользователь добавлен в базу данных");
        }
        catch (HttpServerErrorException exception) {
            log.error("Server error adding new person: {}", exception.getMessage(), exception);
            return ResponseEntity.internalServerError().body("Ошибка сервера при добавлении нового пользователя");
        }
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException exception) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exception.getMessage(),
                                                                          System.currentTimeMillis());
        return ResponseEntity.badRequest().body(personErrorResponse);
    }
}