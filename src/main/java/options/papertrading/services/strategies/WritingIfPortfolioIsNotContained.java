package options.papertrading.services.strategies;

import kafka.producer.model.dto.TradeCreatedEventDto;
import kafka.producer.service.ProducerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.PortfolioConverter;
import options.papertrading.util.exceptions.InsufficientFundsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WritingIfPortfolioIsNotContained implements AdditionStrategy {
    private final PortfolioConverter portfolioConverter;
    private final IPositionSetter positionSetter;
    private final ProducerService producerService;
    private final IJournalFacade journalService;

    @Override
    @Transactional
    public void addPortfolio(@NonNull OptionDto optionDto) {
        Portfolio portfolio = portfolioConverter.convertOptionDtoToPortfolio(optionDto);
        if (positionSetter.isThereEnoughMoneyForWrite(portfolio)) {

            portfolio.setVolume(optionDto.getVolume() * -1);
            portfolio.setTradePrice(positionSetter.countPriceInRur(portfolio));
            portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
            portfolio.setCollateralWhenWasTrade(portfolio.getOption().getWriteCollateral());
            portfolio.getOwner().setCurrentNetPosition(portfolio.getOwner().getCurrentNetPosition()
                    + portfolio.getCollateralWhenWasTrade() * optionDto.getVolume());
            portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                    - portfolio.getCollateralWhenWasTrade() * optionDto.getVolume());
            log.info("Final portfolio: {}", portfolio);

            producerService.createEvent(new TradeCreatedEventDto(null, portfolio));
            journalService.addJournal(portfolio, optionDto);
        } else {
            log.info("Insufficient funds to complete the trade");
            throw new InsufficientFundsException();
        }
    }

    @Override
    public AdditionStrategyType getType() {
        return AdditionStrategyType.WRITING_IF_NOT_CONTAINED;
    }
}
