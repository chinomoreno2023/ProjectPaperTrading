package options.papertrading.facade.interfaces;

import options.papertrading.dto.option.OptionDto;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface IJsonOptionFacade {
    List<OptionDto> showOptionsList();

    void volumeValidate(OptionDto optionDto, BindingResult bindingResult);
}
