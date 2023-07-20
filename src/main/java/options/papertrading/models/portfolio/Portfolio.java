package options.papertrading.models.portfolio;

public class Portfolio {
    private int id;
    private String optionId;
    private int volume;
    private int strike;
    private String type;
    private double price;
    private double volatility;
    private int daysToMaturity;
    private double tradePrice;
    private double volatilityWhenWasTrade;
    private double variatMargin;
    private double collateralWhenWasTrade;
    private double currentNetPosition = 260000.0;
    private double openLimit = 1000000.0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getStrike() {
        return strike;
    }

    public void setStrike(int strike) {
        this.strike = strike;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    public int getDaysToMaturity() {
        return daysToMaturity;
    }

    public void setDaysToMaturity(int daysToMaturity) {
        this.daysToMaturity = daysToMaturity;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public double getVolatilityWhenWasTrade() {
        return volatilityWhenWasTrade;
    }

    public void setVolatilityWhenWasTrade(double volatilityWhenWasTrade) {
        this.volatilityWhenWasTrade = volatilityWhenWasTrade;
    }

    public double getVariatMargin() {
        return variatMargin;
    }

    public void setVariatMargin(double variatMargin) {
        this.variatMargin = variatMargin;
    }

    public double getCollateralWhenWasTrade() {
        return collateralWhenWasTrade;
    }

    public void setCollateralWhenWasTrade(double collateralWhenWasTrade) {
        this.collateralWhenWasTrade = collateralWhenWasTrade;
    }

    public double getCurrentNetPosition() {
        return currentNetPosition;
    }

    public void setCurrentNetPosition(double currentNetPosition) {
        this.currentNetPosition = currentNetPosition;
    }

    public double getOpenLimit() {
        return openLimit;
    }

    public void setOpenLimit(double openLimit) {
        this.openLimit = openLimit;
    }
}