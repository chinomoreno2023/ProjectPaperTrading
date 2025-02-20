package options.papertrading.dto.portfolio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PortfolioDto {
    private String id;
    private int strike;
    private String type;
    private int volume;
    private int daysToMaturity;
    private double tradePrice;
    private double price;
    private double volatilityWhenWasTrade;
    private double volatility;
    private double collateralWhenWasTrade;
    private double buyCollateral;
    private double writeCollateral;
    private double variatMargin;
    private double currentNetPosition;
    private double openLimit;
    private double stepPrice;
}