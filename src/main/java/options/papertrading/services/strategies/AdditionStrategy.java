package options.papertrading.services.strategies;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.AdditionStrategyType;

public interface AdditionStrategy {
    void addPortfolio(OptionDto optionDto);
    AdditionStrategyType getType();
}
