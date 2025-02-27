package options.papertrading.util.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PersonErrorResponse {
    private String message;
    private long timestamp;
}
