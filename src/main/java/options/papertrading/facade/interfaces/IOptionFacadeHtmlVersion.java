package options.papertrading.facade.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import java.util.List;

public interface IOptionFacadeHtmlVersion extends IOptionFacade {
    List<OptionDto> showOptionsListByPrefix(String prefix);
    List<OptionDto> showOptionsFromCurrentPortfolios(List<PortfolioDto> portfolios);
}
