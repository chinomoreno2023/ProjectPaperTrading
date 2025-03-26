package options.papertrading.facade.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import java.util.List;

public interface IOptionFacade {
    List<OptionDto> showOptionsList();
    Option findByOptionId(String id);
}
