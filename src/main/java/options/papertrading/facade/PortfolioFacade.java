package options.papertrading.facade;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPortfolioFacade;
import org.springframework.stereotype.Component;
import java.util.List;

@AllArgsConstructor
@Component
public class PortfolioFacade {
    private final IPortfolioFacade portfolioFacade;

    public List<PortfolioDto> showAllPortfolios() {
        return portfolioFacade.showAllPortfolios();
    }

    public void addPortfolio(OptionDto optionDto) {
        portfolioFacade.addPortfolio(optionDto);
    }

    public void addPortfolio(String id, int volume, int buyOrWrite) {
        portfolioFacade.addPortfolio(id, volume, buyOrWrite);
    }

    public void reset() {
        portfolioFacade.reset();
    }
}