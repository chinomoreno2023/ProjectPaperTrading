package options.papertrading.services;

import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PortfoliosService {
    private final PortfoliosRepository portfoliosRepository;

    @Autowired
    public PortfoliosService(PortfoliosRepository portfoliosRepository) {
        this.portfoliosRepository = portfoliosRepository;
    }

    public List<Portfolio> findAll() {
        return portfoliosRepository.findAll();
    }

    public Portfolio findOne(int id) {
        return portfoliosRepository.findById(id).orElse(null);
    }

    @Transactional
    @Modifying
    public void save(Portfolio portfolio) {
        portfoliosRepository.save(portfolio);
    }

    @Transactional
    @Modifying
    public void delete(int id) {
        portfoliosRepository.deleteById(id);
    }

    @Transactional
    public List<Portfolio> indexWithJoin() {
        return portfoliosRepository.indexWithJoin();
    }
}