package options.papertrading.models.portfolio;

import lombok.Data;
import options.papertrading.models.option.Option;
import options.papertrading.models.users.Person;
import org.hibernate.annotations.Cascade;
import javax.persistence.*;

@Entity
@Data
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "volume")
    private int volume;

    @Column(name = "trade_price", updatable = false)
    private double tradePrice;

    @Column(name = "volatility_when_was_trade", updatable = false)
    private double volatilityWhenWasTrade;

    @Column(name = "variat_margin")
    private double variatMargin;

    @Column(name = "collateral_when_was_trade", updatable = false)
    private double collateralWhenWasTrade;

    @Cascade(org.hibernate.annotations.CascadeType.REFRESH)
    @ManyToOne
    @JoinColumn(name = "option_id", referencedColumnName = "id")
    private Option option;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;
}