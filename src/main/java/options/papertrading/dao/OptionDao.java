package options.papertrading.dao;

import options.papertrading.models.option.Option;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.models.users.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OptionDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OptionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Option> index() {
        return jdbcTemplate.query("SELECT * FROM options", new BeanPropertyRowMapper<>(Option.class));
    }

    public void addOption(Portfolio portfolio) {
        jdbcTemplate.update("INSERT INTO portfolios(option_id, volume, trade_price, " +
                                 "volatility_when_was_trade, variat_margin, collateral_when_was_trade)" +
                                 " VALUES(?, ?, ?, ?, ?, ?)",
                                 portfolio.getId(), portfolio.getVolume(),
                                 portfolio.getTradePrice(), portfolio.getVolatilityWhenWasTrade(),
                                 portfolio.getVariatMargin(), portfolio.getCollateralWhenWasTrade());
    }

    public Option findOptionById(String optionId) {
        return jdbcTemplate.query("SELECT * FROM options WHERE option_id = ?", new Object[]{optionId},
                new BeanPropertyRowMapper<>(Option.class)).stream().findAny().orElse(null);
    }



























    public Person show(int id) {
        return jdbcTemplate.query("SELECT * FROM person WHERE id = ?", new Object[]{id},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny().orElse(null);
    }

    public Optional<Person> show(String email) {
        return jdbcTemplate.query("SELECT * FROM person WHERE email = ?", new Object[]{email},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }

    public Optional<Person> showName(String name) {
        return jdbcTemplate.query("SELECT * FROM person WHERE name = ?", new Object[]{name},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }

//    public void save(Person person) {
//        jdbcTemplate.update("INSERT INTO person(name, age, email, address) VALUES(?, ?, ?, ?)",
//                person.getName(), person.getAge(), person.getEmail(), person.getAddress());
//    }
//
//    public void update(int id, Person updatedPerson) {
//        jdbcTemplate.update("UPDATE person SET name=?, age=?, email=?, address=? WHERE id=?",
//                updatedPerson.getName(), updatedPerson.getAge(), updatedPerson.getEmail(), updatedPerson.getAddress(), id);
//    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM person WHERE id=?", id);
    }
}
