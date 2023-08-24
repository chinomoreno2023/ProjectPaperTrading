package options.papertrading.repositories;

import options.papertrading.models.users.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonsRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
}