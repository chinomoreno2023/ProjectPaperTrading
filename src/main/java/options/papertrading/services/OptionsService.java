package options.papertrading.services;

import options.papertrading.models.option.Option;
import options.papertrading.repositories.OptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OptionsService {
    private final OptionsRepository optionsRepository;

    @Autowired
    public OptionsService(OptionsRepository optionsRepository) {
        this.optionsRepository = optionsRepository;
    }

    public List<Option> findAll() {
        return optionsRepository.findAll();
    }

    public Option findOne(String id) {
        return optionsRepository.findById(id).orElse(null);
    }
}
