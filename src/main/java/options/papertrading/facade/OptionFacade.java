package options.papertrading.facade;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IOptionFacade;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import java.util.List;

@Component
@AllArgsConstructor
public class OptionFacade {
    private final IOptionFacade optionFacade;

    public List<OptionDto> showOptionsList() {
        return optionFacade.showOptionsList();
    }

    public List<OptionDto> showOptionsListByPrefix(String prefix) {
        return optionFacade.showOptionsListByPrefix(prefix);
    }

    public void volumeValidate(OptionDto optionDto, BindingResult bindingResult) {
        optionFacade.volumeValidate(optionDto, bindingResult);
    }

    public List<OptionDto> showOptionsFromCurrentPortfolios(List<PortfolioDto> portfolios) {
        return optionFacade.showOptionsFromCurrentPortfolios(portfolios);
    }
}