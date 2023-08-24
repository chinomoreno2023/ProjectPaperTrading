package options.papertrading.services;

import options.papertrading.models.users.Person;
import options.papertrading.repositories.PersonsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final PersonsRepository personsRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PersonsRepository personsRepository, PasswordEncoder passwordEncoder) {
        this.personsRepository = personsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Modifying
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personsRepository.save(person);
    }
}
