package kafka.producer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import options.papertrading.models.portfolio.Portfolio;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeCreatedEventDto {
    private Portfolio portfolioForDelete;
    private Portfolio portfolioForSave;
}
