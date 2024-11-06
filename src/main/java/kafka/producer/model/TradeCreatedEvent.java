package kafka.producer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import options.papertrading.models.portfolio.Portfolio;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeCreatedEvent implements Serializable {
    private String eventId;
    private Portfolio portfolioForDelete;
    private Portfolio portfolioForSave;
}
