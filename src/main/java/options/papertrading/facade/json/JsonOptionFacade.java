package options.papertrading.facade.json;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.services.OptionsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import java.util.List;

@Component
@AllArgsConstructor
public class JsonOptionFacade {
    OptionsService optionsService;

    public List<OptionDto> showOptionsList() {
        return optionsService.showOptionsList();
    }

    public void volumeValidate(OptionDto optionDto, BindingResult bindingResult) {
        optionsService.validate(optionDto, bindingResult);
    }
}