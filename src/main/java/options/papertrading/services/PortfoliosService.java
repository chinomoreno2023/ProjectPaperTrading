package options.papertrading.services;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.exceptions.InsufficientFundsException;
import options.papertrading.util.mappers.PortfolioMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PortfoliosService {
    private final PortfoliosRepository portfoliosRepository;
    private final PortfolioMapper portfolioMapper;
    private final PersonsService personsService;
    private final OptionsService optionsService;
    private final PositionSetter positionSetter;

    @Transactional(readOnly = true)
    public Portfolio findByOwnerAndOption(Person owner, Option option) {
        return portfoliosRepository.findByOwnerAndOption(owner, option);
    }

    public PortfolioDto convertToPortfolioDto(Portfolio portfolio) {
        return portfolioMapper.convertToPortfolioDto(portfolio);
    }

    public List<PortfolioDto> convertListToPortfolioDtoList(List<Portfolio> portfolios) {
        return portfolios.stream()
                         .map(this::convertToPortfolioDto)
                         .collect(Collectors.toList());
    }

    @Transactional
    @Modifying
    public List<PortfolioDto> showAllPortfolios() {
        return convertListToPortfolioDtoList(positionSetter.refreshDataInPortfolios());
    }

    @Transactional(readOnly = true)
    public Portfolio convertOptionDtoToPortfolio (OptionDto optionDto) {
        Option option = optionsService.findByStrikeAndTypeAndDaysToMaturity(optionDto.getStrike(),
                                                                                  optionDto.getType(),
                                                                                  optionDto.getDaysToMaturity());
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(optionDto.getVolume());
        portfolio.setOwner(personsService.getCurrentPerson());
        portfolio.setOption(option);
        return portfolio;
    }

    @Transactional
    @Modifying
    public void addPortfolio(OptionDto optionDto) {
        if (positionSetter.isContained(convertOptionDtoToPortfolio(optionDto)))
            addPortfolioIfItIsContained(optionDto);
        else addPortfolioIfItIsNotContained(optionDto);
    }

    @Transactional
    @Modifying
    public void addPortfolioIfItIsNotContained(OptionDto optionDto) {
        Portfolio portfolio = convertOptionDtoToPortfolio(optionDto);
        switch (optionDto.getBuyOrWrite()) {
            case 1:
                if (positionSetter.isThereEnoughMoneyForBuy(portfolio)) {
                    portfolio.setTradePrice(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100);
                    portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getBuyCollateral());
                    portfoliosRepository.save(portfolio);
                }
                else throw new InsufficientFundsException();
                break;
            case -1:
                if (positionSetter.isThereEnoughMoneyForWrite(portfolio)) {
                    portfolio.setVolume(optionDto.getVolume() * -1);
                    portfolio.setTradePrice(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100);
                    portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getWriteCollateral());
                    portfoliosRepository.save(portfolio);
                }
                else throw new InsufficientFundsException();
                break;
        }
        positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void addPortfolioIfItIsContained(OptionDto optionDto) {
        Portfolio newPortfolio = convertOptionDtoToPortfolio(optionDto);
        Portfolio oldPortfolio = findByOwnerAndOption(personsService.getCurrentPerson(),
                                                      optionsService.findByOptionId(newPortfolio.getOption().getId()));
        switch (optionDto.getBuyOrWrite()) {
            case 1:
                if (positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)) {
                    directBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
                }
                else reverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
                break;
            case -1:
                if (positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)) {
                    newPortfolio.setVolume(optionDto.getVolume() * -1);
                    directWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
                }
                else {
                    newPortfolio.setVolume(optionDto.getVolume() * -1);
                    reverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
                }
                break;
        }
    }

    @Transactional
    @Modifying
    public void directBuyPortfolioIfItIsContained(Portfolio newPortfolio, Portfolio oldPortfolio) {
        if (positionSetter.isThereEnoughMoneyForBuy(newPortfolio)) {
            newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
            newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else throw new InsufficientFundsException();
        positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void reverseBuyPortfolioIfItIsContained(Portfolio newPortfolio, Portfolio oldPortfolio) {
        if (newPortfolio.getVolume() == Math.abs(oldPortfolio.getVolume())) {
            portfoliosRepository.delete(oldPortfolio);
        }
        else if (newPortfolio.getVolume() > Math.abs(oldPortfolio.getVolume())) {
            if (positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio)) {
                newPortfolio.setVolume(newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
                newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
                newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
                newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
                portfoliosRepository.delete(oldPortfolio);
                portfoliosRepository.save(newPortfolio);
            }
            throw new InsufficientFundsException();
        }

        else if (newPortfolio.getVolume() < Math.abs(oldPortfolio.getVolume())) {
            newPortfolio.setVolume(newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
            newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void directWritePortfolioIfItIsContained(Portfolio newPortfolio, Portfolio oldPortfolio) {
        if (positionSetter.isThereEnoughMoneyForWrite(newPortfolio)) {
            newPortfolio.setVolume((Math.abs(newPortfolio.getVolume()) + Math.abs(oldPortfolio.getVolume())) * -1);
            newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else {
            throw new InsufficientFundsException();
        }
        positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void reverseWritePortfolioIfItIsContained(Portfolio newPortfolio, Portfolio oldPortfolio) {
        if (Math.abs(newPortfolio.getVolume()) == oldPortfolio.getVolume()) {
            portfoliosRepository.delete(oldPortfolio);
        }
        else if (Math.abs(newPortfolio.getVolume()) > oldPortfolio.getVolume()) {
            if (positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio)) {
                newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
                newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
                newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
                newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
                portfoliosRepository.delete(oldPortfolio);
                portfoliosRepository.save(newPortfolio);
            }
            throw new InsufficientFundsException();
        }

        else if (Math.abs(newPortfolio.getVolume()) < oldPortfolio.getVolume()) {
            newPortfolio.setVolume(oldPortfolio.getVolume() + newPortfolio.getVolume());
            newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        positionSetter.refreshDataInPortfolios();
    }
}