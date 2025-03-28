package options.papertrading.facade.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import java.util.List;

public interface IPortfolioFacade {
    List<PortfolioDto> showAllPortfolios();
    void addPortfolio(OptionDto optionDto);
}
