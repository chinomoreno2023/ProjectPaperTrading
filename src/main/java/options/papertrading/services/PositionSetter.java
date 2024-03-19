package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PersonsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PositionSetter {
    private final PersonsRepository personsRepository;
    private final double deposit = 1000000;
    private final EntityManager entityManager;
    private final PersonsService personsService;
    private final PortfoliosRepository portfoliosRepository;

    @Transactional
    @Modifying
    public boolean isThereEnoughMoneyForBuy(@NonNull Portfolio portfolio) {
        refreshDataInPortfolios();
        final double moneyThatYouHave = portfolio.getOwner().getOpenLimit();
        final double moneyThatYouNeed = portfolio.getOption().getBuyCollateral() * portfolio.getVolume();
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed ?
                true :
                false;
    }

    @Transactional
    @Modifying
    public boolean isThereEnoughMoneyForWrite(@NonNull Portfolio portfolio) {
        refreshDataInPortfolios();
        final double moneyThatYouHave = portfolio.getOwner().getOpenLimit();
        final double moneyThatYouNeed = portfolio.getOption().getWriteCollateral() * Math.abs(portfolio.getVolume());
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed ?
                true :
                false;
    }

    @Transactional(readOnly = true)
    public boolean isContained(@NonNull Portfolio portfolio) {
        log.info("Is portfolio '{}' contained?", portfolio);
        return portfoliosRepository.findAllByOwner(portfolio.getOwner())
                .stream()
                .anyMatch(item -> portfolio.getOption().getId().equals(item.getOption().getId()));
    }

    @Transactional
    @Modifying
    public void refreshDataInPortfolios() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        owner.setCurrentNetPosition(0);
        double allVariatMargins = 0;
        if (!optionsInPortfolio.isEmpty()) {
            for (Portfolio portfolio : optionsInPortfolio) {
                entityManager.refresh(portfolio.getOption());
                if (portfolio.getVolume() > 0) {
                    portfolio.setVariatMargin(updateVariatMargin(portfolio));
                    allVariatMargins = allVariatMargins + portfolio.getVariatMargin();
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getBuyCollateral()
                            * portfolio.getVolume());
                } else {
                    portfolio.setVariatMargin(updateVariatMargin(portfolio) * -1);
                    allVariatMargins = allVariatMargins + portfolio.getVariatMargin();
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getWriteCollateral()
                            * Math.abs(portfolio.getVolume()));
                }
            }
        }
        owner.setOpenLimit(deposit - owner.getCurrentNetPosition() + allVariatMargins);
        owner.setOptionsInPortfolio(optionsInPortfolio);
        log.info("Refreshed options in portfolio: {}", optionsInPortfolio);
        personsRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public double updateVariatMargin(@NonNull Portfolio portfolio) {
        portfolio.setVariatMargin(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100 * Math.abs(portfolio.getVolume())
                - (portfolio.getTradePrice() * Math.abs(portfolio.getVolume())));
        final double updatedVariatMargin = portfolio.getVariatMargin();
        log.info("Updated variat margin is {}", updatedVariatMargin);
        return updatedVariatMargin;
    }

    public boolean checkBuyOrWrite(@NonNull OptionDto optionDto) {
        final int buyOrWrite = optionDto.getBuyOrWrite();
        log.info("OptionDto: {}, buy or write: {}", optionDto, buyOrWrite);
        if (buyOrWrite == 1 || buyOrWrite == -1) {
            return buyOrWrite == 1 ? true : false;
        }
        else throw new IllegalArgumentException("Value mast be 1 or -1");
    }

    public boolean checkDirectOrReverse(@NonNull OptionDto optionDto, @NonNull Portfolio portfolio) {
        log.info("OptionDto: {}, portfolio: {}", optionDto, portfolio);
        if (checkBuyOrWrite(optionDto) && portfolio.getVolume() > 0) return true;
        if (!checkBuyOrWrite(optionDto) && portfolio.getVolume() > 0) return false;
        if (checkBuyOrWrite(optionDto) && portfolio.getVolume() < 0) return false;
        return true;
    }

    public boolean isThereEnoughMoneyForReverseBuy(@NonNull Portfolio oldPortfolio, @NonNull Portfolio newPortfolio) {
        refreshDataInPortfolios();
        final double moneyThatYouHave = oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
                + oldPortfolio.getOwner().getOpenLimit();
        final double moneyThatYouNeed = newPortfolio.getOption().getBuyCollateral()
                * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed ?
                true :
                false;
    }

    public boolean isThereEnoughMoneyForReverseWrite(@NonNull Portfolio oldPortfolio, @NonNull Portfolio newPortfolio) {
        refreshDataInPortfolios();
        final double moneyThatYouHave = newPortfolio.getOwner().getOpenLimit()
                + oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume();
        final double moneyThatYouNeed = newPortfolio.getOption().getWriteCollateral()
                * (Math.abs(newPortfolio.getVolume()) - oldPortfolio.getVolume());
        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
        return moneyThatYouHave >= moneyThatYouNeed ?
                true :
                false;
    }

//    @Transactional
//    @Modifying
//    public boolean isThereEnoughMoneyForBuyForTest(@NonNull Portfolio portfolio) {
//        final double moneyThatYouHave = portfolio.getOwner().getOpenLimit();
//        final double moneyThatYouNeed = portfolio.getOption().getBuyCollateral() * portfolio.getVolume();
//        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
//        return moneyThatYouHave >= moneyThatYouNeed ?
//                true :
//                false;
//    }
//
//    @Transactional
//    @Modifying
//    public boolean isThereEnoughMoneyForWriteForTest(@NonNull Portfolio portfolio) {
//        final double moneyThatYouHave = portfolio.getOwner().getOpenLimit();
//        final double moneyThatYouNeed = portfolio.getOption().getWriteCollateral() * Math.abs(portfolio.getVolume());
//        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
//        return moneyThatYouHave >= moneyThatYouNeed ?
//                true :
//                false;
//    }
//
//    public boolean isThereEnoughMoneyForReverseBuyForTest(@NonNull Portfolio oldPortfolio, @NonNull Portfolio newPortfolio) {
//        final double moneyThatYouHave = oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
//                + oldPortfolio.getOwner().getOpenLimit();
//        final double moneyThatYouNeed = newPortfolio.getOption().getBuyCollateral()
//                * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
//        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
//        return moneyThatYouHave >= moneyThatYouNeed ?
//                true :
//                false;
//    }
//
//    public boolean isThereEnoughMoneyForReverseWriteForTest(@NonNull Portfolio oldPortfolio, @NonNull Portfolio newPortfolio) {
//        final double moneyThatYouHave = newPortfolio.getOwner().getOpenLimit()
//                + oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume();
//        final double moneyThatYouNeed = newPortfolio.getOption().getWriteCollateral()
//                * (Math.abs(newPortfolio.getVolume()) - oldPortfolio.getVolume());
//        log.info("Money that you have: {}. Money that you need: {}", moneyThatYouHave, moneyThatYouNeed);
//        return moneyThatYouHave >= moneyThatYouNeed ?
//                true :
//                false;
//    }
}