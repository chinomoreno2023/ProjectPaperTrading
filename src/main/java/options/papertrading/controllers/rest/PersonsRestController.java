package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.interfaces.IJsonPersonFacade;
import options.papertrading.util.exceptions.PersonErrorResponse;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import org.springframework.http.HttpStatus;
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
    private final IJsonPersonFacade personFacade;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getPersons() {
        final List<PersonDto> personDtoList = personFacade.getPersons();
        log.info("Request to get list of all persons. Result: {}", personDtoList);
        return personDtoList.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT):
                new ResponseEntity<>(personDtoList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody @NonNull PersonDto personDto,
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
            log.info("Person was not created. Error: {}", errorMessage.toString());
            throw new PersonNotCreatedException(errorMessage.toString());
        }
        try {
            personFacade.create(personDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (HttpServerErrorException exception) {
            log.error("Server error adding new person: {}", exception.getMessage(), exception);
            return new ResponseEntity("Server error adding new person", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException exception) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(exception.getMessage(),
                                                                          System.currentTimeMillis());
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}