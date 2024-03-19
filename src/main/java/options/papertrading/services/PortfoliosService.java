package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IJsonPortfolioFacade;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.exceptions.InsufficientFundsException;
import options.papertrading.util.mappers.PortfolioMapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PortfoliosService implements IJsonPortfolioFacade {
    private final PortfoliosRepository portfoliosRepository;
    private final PortfolioMapper portfolioMapper;
    private final PersonsService personsService;
    private final OptionsService optionsService;
    private final PositionSetter positionSetter;
    private final JournalService journalService;

    @Transactional(readOnly = true)
    public Portfolio findByOwnerAndOption(@NonNull Person owner, @NonNull Option option) {
        log.info("Request to find by {} and {}", owner, option);
        return portfoliosRepository.findByOwnerAndOption(owner, option);
    }

    public PortfolioDto convertToPortfolioDto(@NonNull Portfolio portfolio) {
        PortfolioDto portfolioDto = portfolioMapper.convertToPortfolioDto(portfolio);
        log.info("Converting {} to portfolioDto. Result: {}", portfolio, portfolioDto);
        return portfolioDto;
    }

    public List<PortfolioDto> convertListToPortfolioDtoList(@NonNull List<Portfolio> portfolios) {
        List<PortfolioDto> portfolioDtoList = portfolios.stream()
                                                        .map(this::convertToPortfolioDto)
                                                        .collect(Collectors.toList());
        log.info("Converting portfolio list '{}' to portfolioDto list. Result: {} ", portfolios, portfolioDtoList);
        return portfolioDtoList;
    }

    @Transactional
    @Modifying
    public List<PortfolioDto> showAllPortfolios() {
        expirationCheck();
        positionSetter.refreshDataInPortfolios();
        return convertListToPortfolioDtoList(findAllByOwner(personsService.getCurrentPerson()));
    }

    @Transactional(readOnly = true)
    public  List<Portfolio> findAllByOwner(@NonNull Person owner) {
        return portfoliosRepository.findAllByOwner(owner);
    }

    @Transactional(readOnly = true)
    public Portfolio convertOptionDtoToPortfolio (@NonNull OptionDto optionDto) {
        Option option = optionsService.findByStrikeAndTypeAndDaysToMaturity(optionDto.getStrike(),
                                                                                  optionDto.getType(),
                                                                                  optionDto.getDaysToMaturity());
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(optionDto.getVolume());
        portfolio.setOwner(personsService.getCurrentPerson());
        portfolio.setOption(option);
        log.info("Converting optionDto '{}' to portfolio. Result: {}", optionDto, portfolio);
        return portfolio;
    }

    @Transactional(readOnly = true)
    public OptionDto convertPortfolioToOptionDto(Portfolio portfolio) {
        OptionDto optionDto = new OptionDto();
        optionDto.setStrike(portfolio.getOption().getStrike());
        optionDto.setType(portfolio.getOption().getType());
        optionDto.setPrice(portfolio.getOption().getPrice());
        optionDto.setVolume(Math.abs(portfolio.getVolume()));
        optionDto.setVolatility(portfolio.getOption().getVolatility());
        optionDto.setDaysToMaturity(portfolio.getOption().getDaysToMaturity());
        optionDto.setBuyCollateral(portfolio.getOption().getBuyCollateral());
        optionDto.setWriteCollateral(portfolio.getOption().getWriteCollateral());
        log.info("Converting Portfolio '{}' to optionDto. Result: {}", portfolio, optionDto);
        return optionDto;
    }

    @Transactional
    @Modifying
    public void addPortfolio(@NonNull OptionDto optionDto) {
        positionSetter.refreshDataInPortfolios();
        if (optionDto.getBuyOrWrite() == 1 || optionDto.getBuyOrWrite() == -1)

            if (positionSetter.isContained(convertOptionDtoToPortfolio(optionDto)))
                addPortfolioIfItIsContained(optionDto);
            else addPortfolioIfItIsNotContained(optionDto);

        else throw new IllegalArgumentException("Value must be 1 or -1");
    }

    @Transactional
    @Modifying
    public void addPortfolioIfItIsNotContained(@NonNull OptionDto optionDto) {
        Portfolio portfolio = convertOptionDtoToPortfolio(optionDto);
        switch (optionDto.getBuyOrWrite()) {
            case 1:
                if (positionSetter.isThereEnoughMoneyForBuy(portfolio)) {
                    journalService.addJournal(portfolio, optionDto);
                    portfolio.setTradePrice(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100);
                    portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getBuyCollateral());
                    log.info("Final portfolio: {}", portfolio);
                    portfoliosRepository.save(portfolio);
                }
                else {
                    log.info("Insufficient funds to complete the trade");
                    throw new InsufficientFundsException();
                    }
                break;
            case -1:
                if (positionSetter.isThereEnoughMoneyForWrite(portfolio)) {
                    journalService.addJournal(portfolio, optionDto);
                    portfolio.setVolume(optionDto.getVolume() * -1);
                    portfolio.setTradePrice(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100);
                    portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getWriteCollateral());
                    log.info("Final portfolio: {}", portfolio);
                    portfoliosRepository.save(portfolio);
                }
                else {
                    log.info("Insufficient funds to complete the trade");
                    throw new InsufficientFundsException();
                }
                break;
        }
        positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void addPortfolioIfItIsContained(@NonNull OptionDto optionDto) {
        Portfolio newPortfolio = convertOptionDtoToPortfolio(optionDto);
        Portfolio oldPortfolio = findByOwnerAndOption(personsService.getCurrentPerson(),
                                                      optionsService.findByOptionId(newPortfolio.getOption().getId()));
        log.info("Old portfolio is {}", newPortfolio);
        log.info("New portfolio is {}", oldPortfolio);
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
    public void directBuyPortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Direct buying");
        if (positionSetter.isThereEnoughMoneyForBuy(newPortfolio)) {
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(1);
            journalService.addJournal(newPortfolio, optionDto);
            newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
            newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
            log.info("New portfolio {} will be buy", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else {
            log.info("Insufficient funds to complete the trade");
            throw new InsufficientFundsException();
        }
            positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void reverseBuyPortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Reverse buying");
        if (newPortfolio.getVolume() == Math.abs(oldPortfolio.getVolume())) {
            log.info("Portfolio {} will be delete", oldPortfolio);
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(1);
            journalService.addJournal(newPortfolio, optionDto);
            portfoliosRepository.delete(oldPortfolio);
        }
        else if (newPortfolio.getVolume() < Math.abs(oldPortfolio.getVolume())) {
            operationsForReverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
            log.info("New portfolio {} will be buy", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else if (newPortfolio.getVolume() > Math.abs(oldPortfolio.getVolume())) {
            if (positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio)) {
                operationsForReverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
                newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
                log.info("New portfolio {} will be buy", newPortfolio);
                log.info("Old portfolio {} will be delete", oldPortfolio);
                portfoliosRepository.delete(oldPortfolio);
                portfoliosRepository.save(newPortfolio);
            }
            else {
                log.info("Insufficient funds to complete the trade");
                throw new InsufficientFundsException();
            }
        }
        positionSetter.refreshDataInPortfolios();
    }

    private void operationsForReverseBuyPortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
        optionDto.setBuyOrWrite(1);
        journalService.addJournal(newPortfolio, optionDto);
        newPortfolio.setVolume(newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
        newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
        newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
    }

    @Transactional
    @Modifying
    public void directWritePortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Direct writing");
        if (positionSetter.isThereEnoughMoneyForWrite(newPortfolio)) {
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(-1);
            journalService.addJournal(newPortfolio, optionDto);
            newPortfolio.setVolume((Math.abs(newPortfolio.getVolume()) + Math.abs(oldPortfolio.getVolume())) * -1);
            operationsForDirectOrReverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
        }
        else {
            log.info("Insufficient funds to complete the trade");
            throw new InsufficientFundsException();
        }
        positionSetter.refreshDataInPortfolios();
    }

    private void operationsForDirectOrReverseWritePortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
        newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
        newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
        log.info("New portfolio {} will be write", newPortfolio);
        log.info("Old portfolio {} will be delete", oldPortfolio);
        portfoliosRepository.delete(oldPortfolio);
        portfoliosRepository.save(newPortfolio);
    }

    @Transactional
    @Modifying
    public void reverseWritePortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Reverse writing");
        if (Math.abs(newPortfolio.getVolume()) == oldPortfolio.getVolume()) {
            log.info("Portfolio {} will be delete", oldPortfolio);
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(-1);
            journalService.addJournal(newPortfolio, optionDto);
            portfoliosRepository.delete(oldPortfolio);
        }
        else if (Math.abs(newPortfolio.getVolume()) < oldPortfolio.getVolume()) {
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(-1);
            journalService.addJournal(newPortfolio, optionDto);
            newPortfolio.setVolume(oldPortfolio.getVolume() + newPortfolio.getVolume());
            newPortfolio.setTradePrice(newPortfolio.getOption().getPrice() * newPortfolio.getOption().getStepPrice() * 100);
            newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
            log.info("New portfolio {} will be write", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else if (Math.abs(newPortfolio.getVolume()) > oldPortfolio.getVolume()) {
            if (positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio)) {
                OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
                optionDto.setBuyOrWrite(-1);
                journalService.addJournal(newPortfolio, optionDto);
                newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
                operationsForDirectOrReverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
            }
            else {
                log.info("Insufficient funds to complete the trade");
                throw new InsufficientFundsException();
            }
        }
        positionSetter.refreshDataInPortfolios();
    }

    @Transactional
    @Modifying
    public void expirationCheck() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalTime timeToCheck = LocalTime.of(18, 45);
        Person owner = personsService.getCurrentPerson();;
        List<Portfolio> portfolioList = findAllByOwner(owner);

        if (currentTime.toLocalTime().compareTo(timeToCheck) >= 0) {
            portfolioList.stream()
                         .filter(portfolio -> portfolio.getOption().getDaysToMaturity() == 0)
                         .forEach(portfolio -> {
                log.info("Portfolio {} is expired", portfolio);
                OptionDto optionDto = convertPortfolioToOptionDto(portfolio);
                if (portfolio.getVolume() >= 1) {
                    optionDto.setBuyOrWrite(-1);
                }
                if (portfolio.getVolume() <= -1) {
                    optionDto.setBuyOrWrite(1);
                }
                addPortfolioIfItIsContained(optionDto);
                });;
        }
    }
}