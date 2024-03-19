package options.papertrading.dto.option;

import lombok.Data;

@Data
public class OptionDto {
    private int strike;
    private String type;
    private double price;
    private int volume;
    private double volatility;
    private int daysToMaturity;
    private double buyCollateral;
    private double writeCollateral;
    private int buyOrWrite;
}