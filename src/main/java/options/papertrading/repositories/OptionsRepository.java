package options.papertrading.repositories;

import options.papertrading.models.option.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsRepository extends JpaRepository<Option, String> {
    public Option findByStrikeAndTypeAndDaysToMaturity(int strike, String type, int daysToMaturity);
}