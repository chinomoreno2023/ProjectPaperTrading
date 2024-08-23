package options.papertrading.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPortfolioFacade;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.exceptions.InsufficientFundsException;
import options.papertrading.util.mail.SmtpMailSender;
import options.papertrading.util.mappers.PortfolioMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfoliosService implements IPortfolioFacade {
    private final PortfoliosRepository portfoliosRepository;
    private final PortfolioMapper portfolioMapper;
    private final PersonsService personsService;
    private final OptionsService optionsService;
    private final PositionSetter positionSetter;
    private final JournalService journalService;
    private final EntityManager entityManager;
    private final JournalRepository journalRepository;
    private final SmtpMailSender smtpMailSender;
    private final TextConverter textConverter;

    @Value("${pathToMarginCallMail}")
    private String pathToMarginCallMail;

    @Transactional(readOnly = true)
    public Portfolio findByOwnerAndOption(@NonNull Person owner, @NonNull Option option) {
        log.info("Request to find by {} and {}", owner, option);
        return portfoliosRepository.findByOwnerAndOption(owner, option);
    }

    public PortfolioDto convertToPortfolioDto(@NonNull Portfolio portfolio) {
        PortfolioDto portfolioDto = portfolioMapper.convertToPortfolioDto(portfolio);
        portfolioDto.setPrice(positionSetter.countPriceInRur(portfolio));
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

    public List<PortfolioDto> showAllPortfolios() {
        positionSetter.updateCurrentNetPositionAndOpenLimit();
        positionSetter.updateAllVariatMargin();
        marginCallCheck();
        return convertListToPortfolioDtoList(findAllByOwner(personsService.getCurrentPerson()));
    }

    @Transactional(readOnly = true)
    public List<Portfolio> findAllByOwner(@NonNull Person owner) {
        return portfoliosRepository.findAllByOwner(owner);
    }

    public Portfolio convertOptionDtoToPortfolio (@NonNull OptionDto optionDto) {
        Option option = optionsService.findByOptionId(optionDto.getId());
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(optionDto.getVolume());
        portfolio.setOwner(personsService.getCurrentPerson());
        portfolio.setOption(option);
        log.info("Converting optionDto '{}' to portfolio. Result: {}", optionDto, portfolio);
        return portfolio;
    }

    public OptionDto convertPortfolioToOptionDto(Portfolio portfolio) {
        OptionDto optionDto = new OptionDto();
        optionDto.setId(portfolio.getOption().getId());
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
    public void addPortfolio(String id, int volume, int buyOrWrite) {
        addPortfolio(optionsService.createOptionDtoFromView(id, volume, buyOrWrite));
    }

    @Transactional
    public void addPortfolio(@NonNull OptionDto optionDto) {
        Portfolio portfolio = convertOptionDtoToPortfolio(optionDto);

        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime nowInMoscow = ZonedDateTime.now(moscowZone);
        LocalTime currentTimeInMoscow = nowInMoscow.toLocalTime();
        DayOfWeek currentDayOfWeek = nowInMoscow.getDayOfWeek();

        LocalTime ExchangeOpenTime = LocalTime.of(6, 50);
        LocalTime ExchangeCloseTime = LocalTime.of(23, 50);

        if (currentDayOfWeek == DayOfWeek.SATURDAY || currentDayOfWeek == DayOfWeek.SUNDAY ||
                currentTimeInMoscow.isBefore(ExchangeOpenTime) || currentTimeInMoscow.isAfter(ExchangeCloseTime)) {
            throw new IllegalArgumentException("Биржа работает по будням с 6:50 до 23:50 по московскому времени");
        }

        LocalTime expiryCheckTime = LocalTime.of(14, 0);
        if (!currentTimeInMoscow.isBefore(expiryCheckTime) && portfolio.getOption().getDaysToMaturity() <= 0) {
            throw new IllegalArgumentException("Дата исполнения опциона истекла и в ближайшее время он будет удалён");
        }

        if (positionSetter.isContained(portfolio)) {
            addPortfolioIfItIsContained(optionDto);
        } else {
            addPortfolioIfItIsNotContained(optionDto);
        }
    }

    @Transactional
    public void addPortfolioIfItIsNotContained(@NonNull OptionDto optionDto) {
        Portfolio portfolio = convertOptionDtoToPortfolio(optionDto);
        switch (optionDto.getBuyOrWrite()) {
            case 1:
                if (positionSetter.isThereEnoughMoneyForBuy(portfolio)) {
                    journalService.addJournal(portfolio, optionDto);

                    portfolio.setTradePrice(positionSetter.countPriceInRur(portfolio));
                    portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getBuyCollateral());
                    portfolio.getOwner().setCurrentNetPosition(portfolio.getOwner().getCurrentNetPosition()
                            + portfolio.getCollateralWhenWasTrade() * portfolio.getVolume());
                    portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                            - portfolio.getCollateralWhenWasTrade() * portfolio.getVolume());
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
                    portfolio.setTradePrice(positionSetter.countPriceInRur(portfolio));
                    portfolio.setVolatilityWhenWasTrade(portfolio.getOption().getVolatility());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getWriteCollateral());
                    portfolio.getOwner().setCurrentNetPosition(portfolio.getOwner().getCurrentNetPosition()
                            + portfolio.getCollateralWhenWasTrade() * optionDto.getVolume());
                    portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                            - portfolio.getCollateralWhenWasTrade() * optionDto.getVolume());
                    log.info("Final portfolio: {}", portfolio);
                    portfoliosRepository.save(portfolio);
                }
                else {
                    log.info("Insufficient funds to complete the trade");
                    throw new InsufficientFundsException();
                }
                break;
        }
    }

    @Transactional
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
    public void directBuyPortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Direct buying");
        if (positionSetter.isThereEnoughMoneyForBuy(newPortfolio)) {
            positionSetter.updateCurrentNetPositionAndOpenLimit();
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(1);
            journalService.addJournal(newPortfolio, optionDto);

            double savedVariatMargin = oldPortfolio.getVariatMargin();
            newPortfolio.setVariatMargin(positionSetter.updateVariatMargin(oldPortfolio) + savedVariatMargin);
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
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else {
            log.info("Insufficient funds to complete the trade");
            throw new InsufficientFundsException();
        }
    }

    @Transactional
    public void reverseBuyPortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Reverse buying");
        if (newPortfolio.getVolume() == Math.abs(oldPortfolio.getVolume())) {
            positionSetter.updateCurrentNetPositionAndOpenLimit();
            log.info("Portfolio: {}", oldPortfolio);
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(1);
            journalService.addJournal(newPortfolio, optionDto);

            oldPortfolio.setCollateralWhenWasTrade(0);
            oldPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
            double savedVariatMargin = oldPortfolio.getVariatMargin();
            oldPortfolio.setVariatMargin(savedVariatMargin + (positionSetter.updateVariatMargin(oldPortfolio) * (-1)));
            oldPortfolio.setVolume(0);
            oldPortfolio.setTradePrice(positionSetter.countPriceInRur(oldPortfolio));
            portfoliosRepository.save(oldPortfolio);
        }
        else if (newPortfolio.getVolume() < Math.abs(oldPortfolio.getVolume())) {
            positionSetter.updateCurrentNetPositionAndOpenLimit();

            newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
            newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                    - newPortfolio.getCollateralWhenWasTrade() * newPortfolio.getVolume());
            newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                    + newPortfolio.getCollateralWhenWasTrade() * newPortfolio.getVolume());
            finalOperationsForReverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
            log.info("New portfolio {} will be buy", newPortfolio);
            log.info("Old portfolio {} will be delete", oldPortfolio);
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else if (newPortfolio.getVolume() > Math.abs(oldPortfolio.getVolume())) {
            if (positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio)) {
                positionSetter.updateCurrentNetPositionAndOpenLimit();

                newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getBuyCollateral());
                newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                        - oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
                        + newPortfolio.getOption().getBuyCollateral() * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume())));
                newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                        + oldPortfolio.getOption().getWriteCollateral() * Math.abs(oldPortfolio.getVolume())
                        - newPortfolio.getOption().getBuyCollateral() * (newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume())));
                finalOperationsForReverseBuyPortfolioIfItIsContained(newPortfolio, oldPortfolio);
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
    }

    @Transactional
    public void finalOperationsForReverseBuyPortfolioIfItIsContained(@NonNull Portfolio newPortfolio,
                                                                     @NonNull Portfolio oldPortfolio) {
        OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
        optionDto.setBuyOrWrite(1);
        journalService.addJournal(newPortfolio, optionDto);

        double savedVariatMargin = oldPortfolio.getVariatMargin();
        newPortfolio.setVariatMargin(savedVariatMargin + (positionSetter.updateVariatMargin(oldPortfolio) * (-1)));
        newPortfolio.setVolume(newPortfolio.getVolume() - Math.abs(oldPortfolio.getVolume()));
        newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
        newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
    }

    @Transactional
    public void directWritePortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Direct writing");
        if (positionSetter.isThereEnoughMoneyForWrite(newPortfolio)) {
            positionSetter.updateCurrentNetPositionAndOpenLimit();
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(-1);
            journalService.addJournal(newPortfolio, optionDto);

            double savedVariatMargin = oldPortfolio.getVariatMargin();
            newPortfolio.setVariatMargin(savedVariatMargin + (positionSetter.updateVariatMargin(oldPortfolio) * (-1)));
            newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                    + newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
            newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                    - newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
            newPortfolio.setVolume((Math.abs(newPortfolio.getVolume()) + Math.abs(oldPortfolio.getVolume())) * -1);
            finalOperationsForDirectOrReverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
        }
        else {
            log.info("Insufficient funds to complete the trade");
            throw new InsufficientFundsException();
        }
    }

    @Transactional
    public void finalOperationsForDirectOrReverseWritePortfolioIfItIsContained(@NonNull Portfolio newPortfolio,
                                                                               @NonNull Portfolio oldPortfolio) {
        newPortfolio.setTradePrice(positionSetter.countPriceInRur(newPortfolio));
        newPortfolio.setVolatilityWhenWasTrade(newPortfolio.getOption().getVolatility());
        newPortfolio.setCollateralWhenWasTrade(newPortfolio.getOption().getWriteCollateral());
        log.info("New portfolio {} will be write", newPortfolio);
        log.info("Old portfolio {} will be delete", oldPortfolio);
        portfoliosRepository.delete(oldPortfolio);
        portfoliosRepository.save(newPortfolio);
    }

    @Transactional
    public void reverseWritePortfolioIfItIsContained(@NonNull Portfolio newPortfolio, @NonNull Portfolio oldPortfolio) {
        log.info("Reverse writing");
        if (Math.abs(newPortfolio.getVolume()) == oldPortfolio.getVolume()) {
            positionSetter.updateCurrentNetPositionAndOpenLimit();
            log.info("Portfolio: {}", oldPortfolio);
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(-1);
            journalService.addJournal(newPortfolio, optionDto);

            oldPortfolio.setCollateralWhenWasTrade(0);
            oldPortfolio.setVolatilityWhenWasTrade(oldPortfolio.getVolatilityWhenWasTrade());
            double savedVariatMargin = oldPortfolio.getVariatMargin();
            oldPortfolio.setVariatMargin(savedVariatMargin + positionSetter.updateVariatMargin(oldPortfolio));
            oldPortfolio.setVolume(0);
            oldPortfolio.setTradePrice(positionSetter.countPriceInRur(oldPortfolio));
            portfoliosRepository.save(oldPortfolio);
        }
        else if (Math.abs(newPortfolio.getVolume()) < oldPortfolio.getVolume()) {
            positionSetter.updateCurrentNetPositionAndOpenLimit();
            OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
            optionDto.setBuyOrWrite(-1);
            journalService.addJournal(newPortfolio, optionDto);

            double savedVariatMargin = oldPortfolio.getVariatMargin();
            newPortfolio.setVariatMargin(positionSetter.updateVariatMargin(oldPortfolio) + savedVariatMargin);
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
            portfoliosRepository.delete(oldPortfolio);
            portfoliosRepository.save(newPortfolio);
        }
        else if (Math.abs(newPortfolio.getVolume()) > oldPortfolio.getVolume()) {
            if (positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio)) {
                positionSetter.updateCurrentNetPositionAndOpenLimit();
                OptionDto optionDto = convertPortfolioToOptionDto(newPortfolio);
                optionDto.setBuyOrWrite(-1);
                journalService.addJournal(newPortfolio, optionDto);

                double savedVariatMargin = oldPortfolio.getVariatMargin();
                newPortfolio.setVariatMargin(positionSetter.updateVariatMargin(oldPortfolio) + savedVariatMargin);
                newPortfolio.getOwner().setCurrentNetPosition(newPortfolio.getOwner().getCurrentNetPosition()
                        - oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume()
                        + newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
                newPortfolio.getOwner().setOpenLimit(newPortfolio.getOwner().getOpenLimit()
                        + oldPortfolio.getOption().getBuyCollateral() * oldPortfolio.getVolume()
                        - newPortfolio.getOption().getWriteCollateral() * Math.abs(newPortfolio.getVolume()));
                newPortfolio.setVolume(newPortfolio.getVolume() + oldPortfolio.getVolume());
                finalOperationsForDirectOrReverseWritePortfolioIfItIsContained(newPortfolio, oldPortfolio);
            }
            else {
                log.info("Insufficient funds to complete the trade");
                throw new InsufficientFundsException();
            }
        }
    }

    @Scheduled(cron = "0 00 14 * * MON-FRI")
    @Retryable(value = { Exception.class }, maxAttempts = 6, backoff = @Backoff(delay = 10000))
    @Transactional
    public void expirationCheck() {
        List<Portfolio> portfolios = portfoliosRepository.findAll();
        if (!portfolios.isEmpty()) {
            portfolios.forEach(portfolio -> {
                entityManager.refresh(portfolio.getOption());
                OptionDto optionDto = convertPortfolioToOptionDto(portfolio);

                if (portfolio.getOption().getDaysToMaturity() <= 0) {
                    double savedVariatMargin = portfolio.getVariatMargin();
                    if (portfolio.getVolume() < 0) {
                        portfolio.setVariatMargin(savedVariatMargin + (positionSetter.updateVariatMargin(portfolio) * (-1)));
                        portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                                + portfolio.getCollateralWhenWasTrade() * Math.abs(portfolio.getVolume()));
                        optionDto.setBuyOrWrite(1);
                    }
                    if (portfolio.getVolume() > 0) {
                        portfolio.setVariatMargin(positionSetter.updateVariatMargin(portfolio) + savedVariatMargin);
                        portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                                + portfolio.getCollateralWhenWasTrade() * portfolio.getVolume());
                        optionDto.setBuyOrWrite(-1);
                    }

                    journalService.addJournal(portfolio, optionDto);
                    portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit() + portfolio.getVariatMargin());
                    personsService.save(portfolio.getOwner());
                    log.info("Portfolio {} will be deleted", portfolio);
                    portfoliosRepository.delete(portfolio);
                }
            });
        }
    }

    @Transactional
    public void marginCallCheck() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        log.info("Owner: {}, options in portfolio: {}", owner, optionsInPortfolio);

        if (owner.getOpenLimit() < owner.getCurrentNetPosition() * 0.5) {
            int counterForJournals = 0;
            while (owner.getOpenLimit() <= owner.getCurrentNetPosition() * 0.5) {
                Portfolio portfolioWithMinVariat = optionsInPortfolio.stream()
                                                                     .filter(portfolio -> portfolio.getVolume() != 0)
                                                                     .min(Comparator.comparingDouble(Portfolio::getVariatMargin))
                                                                     .orElse(null);
                log.info("Portfolio with min variat margin: {}", portfolioWithMinVariat);
                if (portfolioWithMinVariat == null) {
                    sendMarginCallMail(owner);
                    return;
                }

                OptionDto optionDto = convertPortfolioToOptionDto(portfolioWithMinVariat);
                owner.setOpenLimit(owner.getOpenLimit() + portfolioWithMinVariat.getVariatMargin());
                portfolioWithMinVariat.setVariatMargin(0);

                if (portfolioWithMinVariat.getVolume() >= 1) {
                    portfolioWithMinVariat.setVolume(portfolioWithMinVariat.getVolume() - 1);
                    optionDto.setBuyOrWrite(-1);
                }
                if (portfolioWithMinVariat.getVolume() <= -1) {
                    portfolioWithMinVariat.setVolume(portfolioWithMinVariat.getVolume() + 1);
                    optionDto.setBuyOrWrite(1);
                }

                counterForJournals++;
                optionDto.setVolume(1);
                journalService.addJournal(portfolioWithMinVariat, optionDto);
                owner.setOptionsInPortfolio(optionsInPortfolio);
                personsService.save(owner);
                positionSetter.updateCurrentNetPositionAndOpenLimit();
                positionSetter.updateAllVariatMargin();
            }

            if (counterForJournals > 0) {
                sendMarginCallMail(owner);
            }
        }
    }

    private void sendMarginCallMail(Person owner) {
        String message;
        try {
            message = String.format(textConverter.readMailTextFromFile(pathToMarginCallMail), owner.getUsername());
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        try {
            smtpMailSender.sendHtml(owner.getEmail(), "Margin call", message);
        }
        catch (MessagingException exception) {
            throw new RuntimeException("Ошибка при отправке почты", exception);
        }
    }

    @Transactional
    public void reset() {
        Person owner = personsService.getCurrentPerson();
        journalRepository.deleteAllByOwner(owner);
        portfoliosRepository.deleteAllByOwner(owner);
        owner.setCurrentNetPosition(0);
        owner.setOpenLimit(1000000);
    }
}