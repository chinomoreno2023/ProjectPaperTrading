package options.papertrading.repositories;

import options.papertrading.models.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonsRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
    Person findByActivationCode(String code);

    @Modifying
    @Query("update Person p set p.activationCode = :activationCode where p.email = :email")
    void updateActivationCodeByEmail(@Param("email") String email, @Param("activationCode") String activationCode);

    @Query("SELECT p FROM Person p WHERE p.id = :id")
    Person findById(@Param("id") long id);
}