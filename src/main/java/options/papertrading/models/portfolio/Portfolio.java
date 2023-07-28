package options.papertrading.models.portfolio;

import jakarta.persistence.*;
import options.papertrading.models.option.Option;
import options.papertrading.models.users.Person;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "volume")
    private int volume;

    @Column(name = "trade_price")
    private double tradePrice;

    @Column(name = "volatility_when_was_trade")
    private double volatilityWhenWasTrade;

    @Column(name = "variat_margin")
    private double variatMargin;

    @Column(name = "collateral_when_was_trade")
    private double collateralWhenWasTrade;

    @Cascade(org.hibernate.annotations.CascadeType.REFRESH)
    @OneToOne
    @JoinColumn(name = "option_id", referencedColumnName = "id")
    private Option option;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;

    public Portfolio() { }

    public Portfolio(Option option, int volume, double tradePrice, double volatilityWhenWasTrade,
                     double variatMargin, double collateralWhenWasTrade) {
        this.volume = volume;
        this.tradePrice = tradePrice;
        this.volatilityWhenWasTrade = volatilityWhenWasTrade;
        this.variatMargin = variatMargin;
        this.collateralWhenWasTrade = collateralWhenWasTrade;
        this.option = option;
    }

    public Portfolio(Person owner, Option option, int volume, double tradePrice, double volatilityWhenWasTrade,
                     double variatMargin, double collateralWhenWasTrade) {
        this.volume = volume;
        this.tradePrice = tradePrice;
        this.volatilityWhenWasTrade = volatilityWhenWasTrade;
        this.variatMargin = variatMargin;
        this.collateralWhenWasTrade = collateralWhenWasTrade;
        this.option = option;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
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

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", volume=" + volume +
                ", tradePrice=" + tradePrice +
                ", volatilityWhenWasTrade=" + volatilityWhenWasTrade +
                ", variatMargin=" + variatMargin +
                ", collateralWhenWasTrade=" + collateralWhenWasTrade +
                ", option=" + option +
                '}' + getOwner().getName();
    }
}