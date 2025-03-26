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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReverseWritingPortfolioIfItIsContained implements AdditionStrategy {
    private final PortfolioConverter portfolioConverter;
    private final PortfoliosRepository portfoliosRepository;
    private final IPersonFacadeHtmlVersion personsService;
    private final OptionsRepository optionsRepository;
    private final IPositionSetter positionSetter;
    private final IPortfolioManager portfolioManager;
    private final IJournalFacade journalService;
    private final ProducerService producerService;

    @Override
    @Transactional
    public void addPortfolio(@NonNull OptionDto optionDto) {
        Portfolio newPortfolio = portfolioConverter.convertOptionDtoToPortfolio(optionDto);
        Portfolio oldPortfolio = portfoliosRepository.findByOwnerAndOption(
                personsService.getCurrentPerson(),
                optionsRepository.findOneById(newPortfolio.getOption().getId()));
        newPortfolio.setVolume(optionDto.getVolume() * -1);

        log.info("Reverse writing");
        if (Math.abs(newPortfolio.getVolume()) == oldPortfolio.getVolume()) {
            portfolioManager.updateCurrentNetPositionAndOpenLimit();
            log.info("Portfolio: {}", oldPortfolio);

            oldPortfolio.setCollateralWhenWasTrade(0);
            oldPortfolio.setVolatilityWhenWasTrade(oldPortfolio.getVolatilityWhenWasTrade());
            double savedVariatMargin = oldPortfolio.getVariatMargin();
            oldPortfolio.setVariatMargin(savedVariatMargin + portfolioManager.updateVariatMargin(oldPortfolio));
            oldPortfolio.setVolume(0);
            oldPortfolio.setTradePrice(positionSetter.countPriceInRur(oldPortfolio));

            producerService.createEvent(new TradeCreatedEventDto(null, oldPortfolio));
            journalService.addJournal(newPortfolio, optionDto);
        } else if (Math.abs(newPortfolio.getVolume()) < oldPortfolio.getVolume()) {
            portfolioManager.updateCurrentNetPositionAndOpenLimit();

            double savedVariatMargin = oldPortfolio.getVariatMargin();
            newPortfolio.setVariatMargin(portfolioManager.updateVariatMargin(oldPortfolio) + savedVariatMargin);
            newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                    - newPortfolio.getOption().getBuyCollateral() * Math.abs(newPortfolio.getVolume()));
            newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                    + newPortfolio.getOption().getBuyCollateral() * Math.abs(newPortfolio.getVolume()));
            newPortfolio.setVolume(oldPortfolio.getVolume() + newPortfolio.getVolume());
            newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
            log.info("New portfolio {} will be write", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);

            producerService.createEvent(new TradeCreatedEventDto(oldPortfolio, newPortfolio));
            journalService.addJournal(newPortfolio, optionDto);
        } else if (Math.abs(newPortfolio.getVolume()) > oldPortfolio.getVolume()) {
            if (positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio)) {
                portfolioManager.updateCurrentNetPositionAndOpenLimit();

                double savedVariatMargin = oldPortfolio.getVariatMargin();
                newPortfolio.setVariatMargin(portfolioManager.updateVariatMargin(oldPortfolio) + savedVariatMargin);
                newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                        - oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume()
                        + newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
                newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                        + oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume()
                        - newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
                newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
                newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
                newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
                newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
                log.info("New portfolio {} will be write", newPortfolio);
                log.info("Old portfolio {} will be delete", oldPortfolio);

                producerService.createEvent(new TradeCreatedEventDto(oldPortfolio, newPortfolio));
                journalService.addJournal(newPortfolio, optionDto);
            } else {
                log.info("Insufficient funds to complete the trade");
                throw new InsufficientFundsException();
            }
        }
    }
}
