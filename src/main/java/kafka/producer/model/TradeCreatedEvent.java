package kafka.producer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeCreatedEvent {
    private String eventId;
    private String id;
    private int volume;
    private int buyOrWrite;
}
