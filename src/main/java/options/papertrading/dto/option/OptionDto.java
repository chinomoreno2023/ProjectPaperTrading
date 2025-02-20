package options.papertrading.dto.option;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private int strike;
    private String type;
    private double price;
    private int volume;
    private double volatility;
    private int daysToMaturity;
    private double buyCollateral;
    private double writeCollateral;
    private int buyOrWrite;
    private double stepPrice;
}