package options.papertrading.controllers.rest;

import options.papertrading.dto.PersonDto;
import options.papertrading.services.PersonsService;
import options.papertrading.util.exceptions.PersonErrorResponse;
import options.papertrading.util.exceptions.PersonNotCreatedException;
import options.papertrading.util.exceptions.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class PersonsRestController {
    private final PersonsService personsService;

    @Autowired
    public PersonsRestController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @GetMapping
    public List<PersonDto> getPersons() {
        return personsService.findAll()
                             .stream()
                             .map(personsService::convertToPersonDto)
                             .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDto getPerson(@PathVariable("id") int id) {
        return personsService.convertToPersonDto(personsService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDto personDto, BindingResult bindingResult) {
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

        personsService.save(personsService.convertToPerson(personDto));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException exception) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person not found", System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException exception) {
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                exception.getMessage(), System.currentTimeMillis()
        );
        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}