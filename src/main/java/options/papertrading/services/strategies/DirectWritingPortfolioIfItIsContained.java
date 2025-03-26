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
public class DirectWritingPortfolioIfItIsContained implements AdditionStrategy {
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

        log.info("Direct writing");
        if (positionSetter.isThereEnoughMoneyForWrite(newPortfolio)) {
            portfolioManager.updateCurrentNetPositionAndOpenLimit();

            journalService.addJournal(newPortfolio, optionDto);

            double savedVariatMargin = oldPortfolio.getVariatMargin();
            newPortfolio.setVariatMargin(savedVariatMargin + (portfolioManager.updateVariatMargin(oldPortfolio) * (-1)));
            newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                    + newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
            newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                    - newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
            newPortfolio.setVolume((Math.abs(newPortfolio.getVolume()) + Math.abs(oldPortfolio.getVolume())) * -1);
            newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
            log.info("New portfolio {} will be write", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);

            producerService.createEvent(new TradeCreatedEventDto(oldPortfolio, newPortfolio));
        } else {
            log.info("Insufficient funds to complete the trade");
            throw new InsufficientFundsException();
        }
    }
}
