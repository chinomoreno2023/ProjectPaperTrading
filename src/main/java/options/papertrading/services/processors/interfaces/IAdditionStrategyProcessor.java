package options.papertrading.services.processors.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.AdditionStrategyType;

public interface IAdditionStrategyProcessor {
    void process(AdditionStrategyType strategyType, OptionDto optionDto);
}
