package options.papertrading.repositories;

import options.papertrading.models.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PortfoliosRepository extends JpaRepository<Portfolio, Integer> {

    @Query("select p from Portfolio p LEFT JOIN FETCH p.option LEFT JOIN FETCH p.owner")
    public List<Portfolio> indexWithJoin();
}