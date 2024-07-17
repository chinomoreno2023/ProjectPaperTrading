package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PositionSetter {
    private final EntityManager entityManager;
    private final PersonsService personsService;
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
                                   .stream()
                                   .anyMatch(item -> portfolio.getOption().getId().equals(item.getOption().getId()));
    }

    @Transactional
    public void updateCurrentNetPositionAndOpenLimit() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        double savedNetPosition = owner.getCurrentNetPosition();
        owner.setCurrentNetPosition(0);

        if (!optionsInPortfolio.isEmpty()) {
            optionsInPortfolio.forEach(portfolio -> {

                entityManager.refresh(portfolio.getOption());
                if (portfolio.getVolume() > 0) {
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getBuyCollateral()
                            * portfolio.getVolume());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getBuyCollateral());
                }
                if (portfolio.getVolume() < 0) {
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getWriteCollateral()
                            * Math.abs(portfolio.getVolume()));
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getWriteCollateral());
                }
            });

            owner.setOpenLimit(owner.getOpenLimit() - (owner.getCurrentNetPosition() - savedNetPosition));
            owner.setOptionsInPortfolio(optionsInPortfolio);
            log.info("Refreshed portfolios: {}", optionsInPortfolio);
            personsService.save(owner);
        }
    }

    @Scheduled(cron = "0 05 14 * * MON-FRI")
    @Retryable(value = { Exception.class }, maxAttempts = 6, backoff = @Backoff(delay = 10000))
    @Transactional
    public void doClearing() {
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAll();
        if (!optionsInPortfolio.isEmpty()) {
            optionsInPortfolio.forEach(portfolio -> {
                entityManager.refresh(portfolio.getOption());
                double savedVariatMargin = portfolio.getVariatMargin();
                if (portfolio.getVolume() < 0) {
                    portfolio.setVariatMargin(savedVariatMargin + (updateVariatMargin(portfolio) * (-1)));
                }
                if (portfolio.getVolume() > 0) {
                    portfolio.setVariatMargin(updateVariatMargin(portfolio) + savedVariatMargin);
                }
                portfolio.setTradePrice(countPriceInRur(portfolio));
                portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit() + portfolio.getVariatMargin());
                portfolio.setVariatMargin(0);
                personsService.save(portfolio.getOwner());

                if (portfolio.getVolume() == 0) {
                    log.info("Portfolio {} will be deleted", portfolio);
                    portfoliosRepository.delete(portfolio);
                } else {
                    log.info("Refreshed portfolio: {}", portfolio);
                    portfoliosRepository.save(portfolio);
                }
            });
        }
    }

    @Transactional
    @Scheduled(cron = "0 00 19 * * MON-FRI")
    @Retryable(value = { Exception.class }, maxAttempts = 6, backoff = @Backoff(delay = 10000))
    public void doEveningClearing() {
        doClearing();
    }

    @Transactional
    public void updateAllVariatMargin() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        if (!optionsInPortfolio.isEmpty()) {
            optionsInPortfolio
                    .stream()
                    .filter(portfolio -> portfolio.getVolume() != 0)
                    .forEach(portfolio -> {
                             entityManager.refresh(portfolio.getOption());
                             double savedVariatMargin = portfolio.getVariatMargin();
                             if (portfolio.getVolume() < 0) {
                                 portfolio.setVariatMargin(savedVariatMargin + (updateVariatMargin(portfolio) * (-1)));
                             }
                             else {
                                 portfolio.setVariatMargin(updateVariatMargin(portfolio) + savedVariatMargin);
                             }
                             portfolio.setTradePrice(countPriceInRur(portfolio));
                             });
            owner.setOptionsInPortfolio(optionsInPortfolio);
            log.info("Refreshed portfolios: {}", optionsInPortfolio);
            personsService.save(owner);
        }
    }

    @Transactional
    public double updateVariatMargin(@NonNull Portfolio portfolio) {
        portfolio.setVariatMargin(countPriceInRur(portfolio) * Math.abs(portfolio.getVolume())
                - (portfolio.getTradePrice() * Math.abs(portfolio.getVolume())));
        double updatedVariatMargin = portfolio.getVariatMargin();
        log.info("Updated variat margin is {}", updatedVariatMargin);
        return updatedVariatMargin;
    }

    public boolean checkBuyOrWrite(@NonNull OptionDto optionDto) {
        int buyOrWrite = optionDto.getBuyOrWrite();
        log.info("OptionDto: {}, buy or write: {}", optionDto, buyOrWrite);
        if (buyOrWrite == 1 || buyOrWrite == -1) {
            return buyOrWrite == 1;
        }
        else throw new IllegalArgumentException("Value mast be 1 or -1");
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