package options.papertrading.services;

import options.papertrading.dto.PersonDto;
import options.papertrading.models.users.Person;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.util.exceptions.PersonNotFoundException;
import options.papertrading.util.mappers.PersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonsService {
    private final PersonsRepository personsRepository;
    private final PersonMapper personMapper;

    @Autowired
    public PersonsService(PersonsRepository personsRepository, PersonMapper personMapper) {
        this.personsRepository = personsRepository;
        this.personMapper = personMapper;
    }

    public List<Person> findAll() {
        return personsRepository.findAll();
    }

    public Person findOne(int id) {
        return personsRepository.findById(id).orElseThrow(PersonNotFoundException::new);
    }

    public Optional<Person> findByEmail(String email) {
        return personsRepository.findByEmail(email);
    }

    @Transactional
    @Modifying
    public void save(Person person) {
        enrichPerson(person);
        personsRepository.save(person);
    }

    @Transactional
    @Modifying
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        personsRepository.save(updatedPerson);
    }

    @Transactional
    @Modifying
    public void delete(int id) {
        personsRepository.deleteById(id);
    }

    private void enrichPerson(Person person) {
        person.setCurrentNetPosition(0);
        person.setOpenLimit(1000000);
    }

    public Person convertToPerson(PersonDto personDto) {
        Person person = personMapper.convertToPerson(personDto);
        person.setPassword("123");
        return person;
    }

    public PersonDto convertToPersonDto(Person person) {
        return personMapper.convertToPersonDto(person);
    }
}