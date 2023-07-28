package options.papertrading.dao;

import options.papertrading.models.users.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Component
public class PersonDao {
    private final SessionFactory sessionFactory;

    @Autowired
    public PersonDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public List<Person> index() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("select p from Person p", Person.class).getResultList();
    }

    @Transactional(readOnly = true)
    public Person show(int id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Person.class, id);
    }

    @Transactional
    public Optional<Person> showEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Person WHERE email = :email", Person.class)
                                    .setParameter("email", email)
                                    .stream()
                                    .filter(x -> x.getEmail().equals(email))
                                    .findAny();
    }

    @Transactional
    public Optional<Person> showName(String name) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Person WHERE name = :name", Person.class)
                .setParameter("name", name)
                .stream()
                .filter(x -> x.getName().equals(name))
                .findAny();
    }

    @Transactional
    public void save(Person person) {
        Session session = sessionFactory.getCurrentSession();
        session.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        Session session = sessionFactory.getCurrentSession();
        Person personToBeUpdated = session.get(Person.class, id);
        personToBeUpdated.setName(updatedPerson.getName());
        personToBeUpdated.setEmail(updatedPerson.getEmail());
    }

    @Transactional
    public void delete(int id) {
        Session session = sessionFactory.getCurrentSession();
        session.remove(session.get(Person.class, id));
    }
}