package options.papertrading.models.options;

public class Option {
    private String id;
    private int strike;
    private String type;
    private double price;
    private double volatility;
    private int daysToMaturity;
    private double buyCollateral;
    private double writeCollateral;
    private double stepPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getBuyCollateral() {
        return buyCollateral;
    }

    public void setBuyCollateral(double buyCollateral) {
        this.buyCollateral = buyCollateral;
    }

    public double getWriteCollateral() {
        return writeCollateral;
    }

    public void setWriteCollateral(double writeCollateral) {
        this.writeCollateral = writeCollateral;
    }

    public double getStepPrice() {
        return stepPrice;
    }

    public void setStepPrice(double stepPrice) {
        this.stepPrice = stepPrice;
    }
}