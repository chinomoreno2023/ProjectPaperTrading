package options.papertrading.repositories;

import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfoliosRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findAllByOwner(Person owner);
    Portfolio findByOwnerAndOption(Person owner, Option option);
    List<Portfolio> findAll();
}