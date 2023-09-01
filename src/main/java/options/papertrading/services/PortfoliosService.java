package options.papertrading.services;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.models.users.Person;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.mappers.PortfolioMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfoliosService {
    private final PortfoliosRepository portfoliosRepository;
    private final PortfolioMapper portfolioMapper;
    private final PersonsService personsService;
    private final OptionsService optionsService;

    @Transactional(readOnly = true)
    public List<Portfolio> findAll() {
        return portfoliosRepository.findAll();
    }


    public PortfolioDto convertToPortfolioDto(Portfolio portfolio) {
        return portfolioMapper.convertToPortfolioDto(portfolio);
    }


    public List<PortfolioDto> convertListToPortfolioDtoList(List<Portfolio> portfolios, Person person) {
        return portfolios.stream()
                         .filter(item -> item.getOwner().getId() == person.getId())
                         .map(this::convertToPortfolioDto)
                         .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PortfolioDto> showAllPortfolios() {
        final Person person = personsService.getCurrentPerson();
        final List<Portfolio> portfolios = findAll();
        return convertListToPortfolioDtoList(portfolios, person);
    }

    @Transactional(readOnly = true)
    public Portfolio convertOptionDtoToPortfolio (OptionDto optionDto) {
        final Option option = optionsService.findByStrikeAndTypeAndDaysToMaturity(optionDto.getStrike(),
                                                                                  optionDto.getType(),
                                                                                  optionDto.getDaysToMaturity());
        final Portfolio portfolio = new Portfolio();
        portfolio.setOption(option);
        portfolio.setVolume(optionDto.getVolume());
        portfolio.setOwner(personsService.getCurrentPerson());
        return portfolio;
    }

    @Transactional
    @Modifying
    public void addPortfolio(OptionDto optionDto) {
        portfoliosRepository.save(convertOptionDtoToPortfolio(optionDto));
    }
}