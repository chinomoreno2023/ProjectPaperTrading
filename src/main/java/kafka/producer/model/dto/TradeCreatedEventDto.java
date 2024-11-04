package kafka.producer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeCreatedEventDto {
    private String id;
    private int volume;
    private int buyOrWrite;
}
