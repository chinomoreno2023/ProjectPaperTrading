package options.papertrading.repositories;

import options.papertrading.models.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonsRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    Person findByActivationCode(String code);
    void deleteByEmail(String email);
}