package options.papertrading.facade;

import lombok.RequiredArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPortfolioFacadeHtmlVersion;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PortfolioFacade {
    private final IPortfolioFacadeHtmlVersion portfolioFacade;
    private final IPortfolioManager portfolioManager;

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
        portfolioManager.reset();
    }
}