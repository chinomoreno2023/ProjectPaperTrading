package options.papertrading.services.processors.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.portfolio.Portfolio;

public interface IPortfolioManager {
    void reset();
    void expirationCheck();
    void updateCurrentNetPositionAndOpenLimit();
    void doClearing();
    void updateAllVariatMargin();
    double updateVariatMargin(Portfolio portfolio);
    Portfolio checkOrderTime(OptionDto optionDto);
}
