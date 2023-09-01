package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import options.papertrading.dto.person.PersonDto;
import options.papertrading.facade.json.JsonPersonFacade;
import options.papertrading.util.exceptions.PersonErrorResponse;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rest")
@AllArgsConstructor
public class PersonsRestController {
    private final JsonPersonFacade personFacade;

    @GetMapping
    public ResponseEntity<List<PersonDto>> getPersons() {
        final List<PersonDto> personDtoList = personFacade.getPersons();
        return personDtoList.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT):
                new ResponseEntity<>(personDtoList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody PersonDto personDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessage.append(error.getField())
                            .append(" - ").append(error.getDefaultMessage())
                            .append(";");
            }
            throw new PersonNotCreatedException(errorMessage.toString());
        }

        personFacade.create(personDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException exception) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                exception.getMessage(), System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}