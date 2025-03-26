package options.papertrading.services.processors.interfaces;

import options.papertrading.dto.option.OptionDto;

public interface IAdditionStrategyProcessor {
    void process(String strategy, OptionDto optionDto);
}
