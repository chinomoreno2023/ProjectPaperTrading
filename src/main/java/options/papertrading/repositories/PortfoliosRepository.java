package options.papertrading.repositories;

import options.papertrading.models.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfoliosRepository extends JpaRepository<Portfolio, Integer> {
}