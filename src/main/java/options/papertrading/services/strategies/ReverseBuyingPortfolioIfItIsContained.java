package options.papertrading.services.strategies;

import kafka.producer.model.dto.TradeCreatedEventDto;
import kafka.producer.service.ProducerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.PortfolioConverter;
import options.papertrading.util.exceptions.InsufficientFundsException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReverseBuyingPortfolioIfItIsContained implements AdditionStrategy {
    private final PortfolioConverter portfolioConverter;
    private final PortfoliosRepository portfoliosRepository;
    private final IPersonFacadeHtmlVersion personsService;
    private final OptionsRepository optionsRepository;
    private final IPositionSetter positionSetter;
    private final IPortfolioManager portfolioManager;
    private final IJournalFacade journalService;
    private final ProducerService producerService;
    private final BeanFactory beanFactory;

    @Override
    @Transactional
    public void addPortfolio(@NonNull OptionDto optionDto) {
        Portfolio newPortfolio = portfolioConverter.convertOptionDtoToPortfolio(optionDto);
        Portfolio oldPortfolio = portfoliosRepository.findByOwnerAndOption(
                personsService.getCurrentPerson(),
                optionsRepository.findOneById(newPortfolio.getOption().getId()));

        log.info("Reverse buying");
        if (newPortfolio.getVolume() == Math.abs(oldPortfolio.getVolume())) {
            portfolioManager.updateCurrentNetPositionAndOpenLimit();
            log.info("Portfolio: {}", oldPortfolio);

            oldPortfolio.setCollateralWhenWasTrade(0);
            oldPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            double savedVariatMargin = oldPortfolio.getVariatMargin();
            oldPortfolio.setVariatMargin(savedVariatMargin + (portfolioManager.updateVariatMargin(oldPortfolio) * (-1)));
            oldPortfolio.setVolume(0);
            oldPortfolio.setTradePrice(positionSetter.countPriceInRur(oldPortfolio));

            producerService.createEvent(new TradeCreatedEventDto(null, oldPortfolio));
            journalService.addJournal(newPortfolio, optionDto);
        } else if (newPortfolio.getVolume() < Math.abs(oldPortfolio.getVolume())) {
            portfolioManager.updateCurrentNetPositionAndOpenLimit();

            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
            newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                    - newPortfolio.getCollateralWhenWasTrade() * newPortfolio.getVolume());
            newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                    + newPortfolio.getCollateralWhenWasTrade() * newPortfolio.getVolume());
            beanFactory.getBean(ReverseBuyingPortfolioIfItIsContained.class)
                    .doFinalOperations(newPortfolio, oldPortfolio);
            log.info("New portfolio {} will be buy", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);

            producerService.createEvent(new TradeCreatedEventDto(oldPortfolio, newPortfolio));
            journalService.addJournal(newPortfolio, optionDto);
        } else if (newPortfolio.getVolume() > Math.abs(oldPortfolio.getVolume())) {
            if (positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio)) {
                portfolioManager.updateCurrentNetPositionAndOpenLimit();

                newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
                newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                        - oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
                        + newPortfolio.getOption().getBuyCollateral() * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume())));
                newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                        + oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
                        - newPortfolio.getOption().getBuyCollateral() * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume())));
                beanFactory.getBean(ReverseBuyingPortfolioIfItIsContained.class)
                        .doFinalOperations(newPortfolio, oldPortfolio);
                log.info("New portfolio {} will be buy", newPortfolio);
                log.info("Old portfolio {} will be delete", oldPortfolio);

                producerService.createEvent(new TradeCreatedEventDto(oldPortfolio, newPortfolio));
                journalService.addJournal(newPortfolio, optionDto);
            } else {
                log.info("Insufficient funds to complete the trade");
                throw new InsufficientFundsException();
            }
        }
    }

    @Transactional
    public void doFinalOperations(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        OptionDto optionDto = portfolioConverter.convertPortfolioToOptionDto(newPortfolio);
        optionDto.setBuyOrWrite(1);
        double savedVariatMargin = oldPortfolio.getVariatMargin();
        newPortfolio.setVariatMargin(savedVariatMargin + (portfolioManager.updateVariatMargin(oldPortfolio) * (-1)));
        newPortfolio.setVolume(newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
        newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
        newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
    }
}
