package options.papertrading.repositories;

import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    List<Journal> findAllByOwner(Person owner);
    void deleteAllByOwner(Person owner);
}
