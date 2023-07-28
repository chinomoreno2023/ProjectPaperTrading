package options.papertrading.models.option;

import jakarta.persistence.*;
import options.papertrading.models.portfolio.Portfolio;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "strike")
    private int strike;

    @Column(name = "type")
    private String type;

    @Column(name = "price")
    private double price;

    @Column(name = "volatility")
    private double volatility;

    @Column(name = "days_to_maturity")
    private int daysToMaturity;

    @Column(name = "buy_collateral")
    private double buyCollateral;

    @Column(name = "write_collateral")
    private double writeCollateral;

    @Column(name = "step_price")
    private double stepPrice;

    @OneToOne(mappedBy = "option")
    private Portfolio portfolio;

    public Option() { }

    public Option(String id, int strike, String type, double price, double volatility, int daysToMaturity, double buyCollateral,
                  double writeCollateral, double stepPrice) {
        this.id = id;
        this.strike = strike;
        this.type = type;
        this.price = price;
        this.volatility = volatility;
        this.daysToMaturity = daysToMaturity;
        this.buyCollateral = buyCollateral;
        this.writeCollateral = writeCollateral;
        this.stepPrice = stepPrice;
    }

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

    @Override
    public String toString() {
        return "Option{" +
                "id='" + id + '\'' +
                ", strike=" + strike +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", volatility=" + volatility +
                ", daysToMaturity=" + daysToMaturity +
                ", buyCollateral=" + buyCollateral +
                ", writeCollateral=" + writeCollateral +
                ", stepPrice=" + stepPrice;
    }
}