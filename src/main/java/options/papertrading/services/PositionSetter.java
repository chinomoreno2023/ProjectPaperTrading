package options.papertrading.services;

import lombok.AllArgsConstructor;
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
    public boolean isThereEnoughMoneyForBuy(Portfolio portfolio) {
        refreshDataInPortfolios();
        return portfolio.getOwner().getOpenLimit()
                >= portfolio.getOption().getBuyCollateral() * portfolio.getVolume() ?
                true :
                false;
    }

    @Transactional
    @Modifying
    public boolean isThereEnoughMoneyForWrite(Portfolio portfolio) {
        refreshDataInPortfolios();
        return portfolio.getOwner().getOpenLimit()
                >= portfolio.getOption().getWriteCollateral() * Math.abs(portfolio.getVolume()) ?
                true :
                false;
    }

    @Transactional(readOnly = true)
    public boolean isContained(Portfolio portfolio) {
        for (Portfolio item : portfoliosRepository.findAllByOwner(portfolio.getOwner()))
            if (portfolio.getOption().getId().equals(item.getOption().getId()))
                return true;

        return false;
    }

    @Transactional
    @Modifying
    public List<Portfolio> refreshDataInPortfolios() {
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
                }
                else {
                    portfolio.setVariatMargin(updateVariatMargin(portfolio) * -1);
                    allVariatMargins = allVariatMargins + portfolio.getVariatMargin();
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getWriteCollateral()
                            * Math.abs(portfolio.getVolume()));
                }
            }
        }
        owner.setOpenLimit(deposit - owner.getCurrentNetPosition() + allVariatMargins);
        owner.setOptionsInPortfolio(optionsInPortfolio);
        personsRepository.save(owner);
        return optionsInPortfolio;
    }

    @Transactional(readOnly = true)
    public double updateVariatMargin(Portfolio portfolio) {
        portfolio.setVariatMargin(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100 * Math.abs(portfolio.getVolume())
                               - (portfolio.getTradePrice() * Math.abs(portfolio.getVolume())));
        return portfolio.getVariatMargin();
    }

    public boolean checkBuyOrWrite(OptionDto optionDto) {
        return optionDto.getBuyOrWrite() == 1 ? true  : false;
    }

    public boolean checkDirectOrReverse(OptionDto optionDto, Portfolio portfolio) {
        if (checkBuyOrWrite(optionDto) && portfolio.getVolume() > 0) return true;
        if (!checkBuyOrWrite(optionDto) && portfolio.getVolume() > 0) return false;
        if (checkBuyOrWrite(optionDto) && portfolio.getVolume() < 0) return false;
        return true;
    }

    public boolean isThereEnoughMoneyForReverseBuy(Portfolio oldPortfolio, Portfolio newPortfolio) {
        refreshDataInPortfolios();
        return (newPortfolio.getOwner().getOpenLimit()
                + oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume()))
                >= (newPortfolio.getOption().getBuyCollateral()
                * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()))) ?
                true :
                false;
    }

    public boolean isThereEnoughMoneyForReverseWrite(Portfolio oldPortfolio, Portfolio newPortfolio) {
        refreshDataInPortfolios();
        return (newPortfolio.getOwner().getOpenLimit()
                + oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume())
                >= (newPortfolio.getOption().getWriteCollateral()
                * (Math.abs(newPortfolio.getVolume()) - oldPortfolio.getVolume())) ?
                true :
                false;
    }
}