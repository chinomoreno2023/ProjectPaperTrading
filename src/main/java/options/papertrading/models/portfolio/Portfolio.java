package options.papertrading.models.portfolio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    @ManyToOne
    @JoinColumn(name = "option_id", referencedColumnName = "id")
    private Option option;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", volume=" + volume +
                ", tradePrice=" + tradePrice +
                ", volatilityWhenWasTrade=" + volatilityWhenWasTrade +
                ", variatMargin=" + variatMargin +
                ", collateralWhenWasTrade=" + collateralWhenWasTrade +
                ", option=" + option.getId() +
                ", owner=" + owner.getUsername() +
                '}';
    }
}