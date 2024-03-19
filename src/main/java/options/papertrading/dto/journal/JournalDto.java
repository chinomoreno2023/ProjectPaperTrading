package options.papertrading.dto.journal;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JournalDto {
    private LocalDateTime dateAndTime;
    private String optionName;
    private int volume;
    private double price;
    private int buyOrWrite;
}
