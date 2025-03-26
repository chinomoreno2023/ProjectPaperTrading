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

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectBuyingPortfolioIfItIsContained implements AdditionStrategy {
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

        log.info("Direct buying");
        if (positionSetter.isThereEnoughMoneyForBuy(newPortfolio)) {
            portfolioManager.updateCurrentNetPositionAndOpenLimit();

            double savedVariatMargin = oldPortfolio.getVariatMargin();
            newPortfolio.setVariatMargin(portfolioManager.updateVariatMargin(oldPortfolio) + savedVariatMargin);
            newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
            newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
            newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                    + newPortfolio.getCollateralWhenWasTrade() * optionDto.getVolume());
            newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                    - newPortfolio.getCollateralWhenWasTrade() * optionDto.getVolume());
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
