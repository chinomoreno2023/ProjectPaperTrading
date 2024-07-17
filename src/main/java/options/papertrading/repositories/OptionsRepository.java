package options.papertrading.repositories;

import options.papertrading.models.option.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OptionsRepository extends JpaRepository<Option, String> {
    Option findOneById(String id);
    List<Option> findByIdStartingWith(String prefix);
}