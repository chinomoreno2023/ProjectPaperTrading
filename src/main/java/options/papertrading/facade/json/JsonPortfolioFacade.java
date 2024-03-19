package options.papertrading.facade.json;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IJsonPortfolioFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class JsonPortfolioFacade {
    private final IJsonPortfolioFacade portfoliosService;

    public List<PortfolioDto> showAllPortfolios() {
        return portfoliosService.showAllPortfolios();
    }

    public void addPortfolio(OptionDto optionDto) {
        portfoliosService.addPortfolio(optionDto);
    }
}