package options.papertrading.dto.person;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PersonDto {

    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 3, max = 30, message = "Name should be between 3 and 30 characters")
    private String username;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    private String email;

    private String role;
}
