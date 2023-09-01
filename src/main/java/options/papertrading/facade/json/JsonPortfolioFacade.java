package options.papertrading.facade.json;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.services.OptionsService;
import options.papertrading.services.PersonsService;
import options.papertrading.services.PortfoliosService;
import org.springframework.stereotype.Component;
import java.util.List;

@AllArgsConstructor
@Component
public class JsonPortfolioFacade {
    private final PortfoliosService portfoliosService;
    private final PersonsService personsService;
    private final OptionsService optionsService;

    public List<PortfolioDto> showAllPortfolios() {
        return portfoliosService.showAllPortfolios();
    }

    public void addPortfolio(OptionDto optionDto) {
        portfoliosService.addPortfolio(optionDto);
    }
}