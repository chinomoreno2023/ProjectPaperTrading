package options.papertrading.services.processors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.models.option.Option;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionSetter implements IPositionSetter {
    private final PortfoliosRepository portfoliosRepository;

    @Transactional(readOnly = true)
    public boolean isThereEnoughMoneyForBuy(@NonNull Portfolio portfolio) {
        double moneyThatYouHave = portfolio.getOwner().getOpenLimit();
        double moneyThatYouNeed = portfolio.getOption().getBuyCollateral() * portfolio.getVolume();
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed;
    }

    @Transactional(readOnly = true)
    public boolean isThereEnoughMoneyForWrite(@NonNull Portfolio portfolio) {
        double moneyThatYouHave = portfolio.getOwner().getOpenLimit();
        double moneyThatYouNeed = portfolio.getOption().getWriteCollateral() * Math.abs(portfolio.getVolume());
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed;
    }

    @Transactional(readOnly = true)
    public boolean isContained(@NonNull Portfolio portfolio) {
        log.info("Is portfolio '{}' contained?", portfolio);
        return portfoliosRepository.findAllByOwner(portfolio.getOwner())
                                   .parallelStream()
                                   .anyMatch(item -> portfolio.getOption().getId().equals(item.getOption().getId()));
    }

    public boolean checkBuyOrWrite(@NonNull OptionDto optionDto) {
        int buyOrWrite = optionDto.getBuyOrWrite();
        log.info("OptionDto: {}, buy or write: {}", optionDto, buyOrWrite);
        if (buyOrWrite == 1 || buyOrWrite == -1) {
            return buyOrWrite == 1;
        } else throw new IllegalArgumentException("Value mast be 1 or -1");
    }

    @Transactional(readOnly = true)
    public boolean checkDirectOrReverse(@NonNull OptionDto optionDto, @NonNull Portfolio portfolio) {
        log.info("OptionDto: {}, portfolio: {}", optionDto, portfolio);
        if (checkBuyOrWrite(optionDto) && portfolio.getVolume() > 0) return true;
        if (!checkBuyOrWrite(optionDto) && portfolio.getVolume() > 0) return false;
        if (checkBuyOrWrite(optionDto) && portfolio.getVolume() < 0) return false;
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isThereEnoughMoneyForReverseBuy(@NonNull Portfolio oldPortfolio, @NonNull Portfolio newPortfolio) {
        double moneyThatYouHave = oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
                + oldPortfolio.getOwner().getOpenLimit();
        double moneyThatYouNeed = newPortfolio.getOption().getBuyCollateral()
                * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed;
    }

    @Transactional(readOnly = true)
    public boolean isThereEnoughMoneyForReverseWrite(@NonNull Portfolio oldPortfolio, @NonNull Portfolio newPortfolio) {
        double moneyThatYouHave = newPortfolio.getOwner().getOpenLimit()
                + oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume();
        double moneyThatYouNeed = newPortfolio.getOption().getWriteCollateral()
                * (Math.abs(newPortfolio.getVolume()) - oldPortfolio.getVolume());
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed;
    }

    @Transactional(readOnly = true)
    public double countPriceInRur(@NonNull Portfolio portfolio) {
        double priceInRur = 0;
        String optionTicker = portfolio.getOption().getId()
                .substring(0, Math.min(portfolio.getOption().getId().length(), 2));
        log.info("Option ticker: {}", optionTicker);

        priceInRur = switch (optionTicker) {
            case "SF", "BR" -> portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100;
            case "SR", "Si", "MX", "GZ" -> portfolio.getOption().getPrice();
            case "RI" -> (portfolio.getOption().getPrice() / 10) * portfolio.getOption().getStepPrice();
            default -> priceInRur;
        };
        log.info("Price in RUR: {}", priceInRur);
        return priceInRur;
    }

    @Transactional(readOnly = true)
    public double countPriceInRur(@NonNull Option option) {
        double priceInRur = 0;
        String optionTicker = option.getId().substring(0, Math.min(option.getId().length(), 2));
        log.info("Option ticker: {}", optionTicker);

        priceInRur = switch (optionTicker) {
            case "SF", "BR" -> option.getPrice() * option.getStepPrice() * 100;
            case "SR", "Si", "MX", "GZ" -> option.getPrice();
            case "RI" -> (option.getPrice() / 10) * option.getStepPrice();
            default -> priceInRur;
        };
        log.info("Price in RUR: {}", priceInRur);
        return priceInRur;
    }
}