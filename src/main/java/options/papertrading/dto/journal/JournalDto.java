package options.papertrading.dto.journal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class JournalDto {
    private LocalDateTime dateAndTime;
    private String optionName;
    private int volume;
    private double price;
    private int buyOrWrite;
}
