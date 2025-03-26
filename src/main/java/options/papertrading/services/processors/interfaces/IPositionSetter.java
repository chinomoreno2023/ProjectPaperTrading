package options.papertrading.services.processors.interfaces;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.portfolio.Portfolio;

public interface IPositionSetter {
    boolean isThereEnoughMoneyForBuy(Portfolio portfolio);
    boolean isThereEnoughMoneyForWrite(Portfolio portfolio);
    boolean isContained(Portfolio portfolio);
    boolean checkBuyOrWrite(OptionDto optionDto);
    boolean checkDirectOrReverse(OptionDto optionDto, Portfolio portfolio);
    boolean isThereEnoughMoneyForReverseBuy(Portfolio oldPortfolio, Portfolio newPortfolio);
    boolean isThereEnoughMoneyForReverseWrite(Portfolio oldPortfolio, Portfolio newPortfolio);
    double countPriceInRur(Portfolio portfolio);
    double countPriceInRur(Option option);
}
