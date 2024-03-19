package options.papertrading.facade.json;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJsonOptionFacade;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

@Component
@AllArgsConstructor
public class JsonOptionFacade {
    private final IJsonOptionFacade optionsService;

    public List<OptionDto> showOptionsList() {
        return optionsService.showOptionsList();
    }

    public void volumeValidate(OptionDto optionDto, BindingResult bindingResult) {
        optionsService.volumeValidate(optionDto, bindingResult);
    }
}