package options.papertrading.models.option;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import options.papertrading.models.portfolio.Portfolio;

import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "options")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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

    @OneToMany(mappedBy = "option")
    private List<Portfolio> portfolio;

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
                ", stepPrice=" + stepPrice +
                '}';
    }
}