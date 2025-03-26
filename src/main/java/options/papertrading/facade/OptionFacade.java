package options.papertrading.facade;

import lombok.RequiredArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IOptionFacadeHtmlVersion;
import options.papertrading.util.validators.VolumeValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OptionFacade {
    private final IOptionFacadeHtmlVersion optionFacade;
    private final VolumeValidator volumeValidator;

    public List<OptionDto> showOptionsList() {
        return optionFacade.showOptionsList();
    }

    public List<OptionDto> showOptionsListByPrefix(String prefix) {
        return optionFacade.showOptionsListByPrefix(prefix);
    }

    public void volumeValidate(OptionDto optionDto, BindingResult bindingResult) {
        volumeValidator.validate(optionDto, bindingResult);
    }

    public List<OptionDto> showOptionsFromCurrentPortfolios(List<PortfolioDto> portfolios) {
        return optionFacade.showOptionsFromCurrentPortfolios(portfolios);
    }
}