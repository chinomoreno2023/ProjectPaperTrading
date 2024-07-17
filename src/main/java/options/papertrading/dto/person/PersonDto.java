package options.papertrading.dto.person;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class PersonDto {

    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 3, max = 30, message = "Имя должно быть от 3 до 30 символов")
    private String username;

    @NotEmpty(message = "Вы забыли ввести почту")
    @Email(message = "Вы ввели некорректную почту")
    @Size(max = 50, message = "Длина почты не должна превышать 30 символов")
    private String email;

    private String role;
}
