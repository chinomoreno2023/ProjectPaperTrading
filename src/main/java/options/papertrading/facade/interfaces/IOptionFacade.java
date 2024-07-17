package options.papertrading.facade.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import org.springframework.validation.BindingResult;
import java.util.List;

public interface IOptionFacade {
    List<OptionDto> showOptionsList();
    List<OptionDto> showOptionsListByPrefix(String prefix);
    List<OptionDto> showOptionsFromCurrentPortfolios(List<PortfolioDto> portfolios);
    void volumeValidate(OptionDto optionDto, BindingResult bindingResult);
}
