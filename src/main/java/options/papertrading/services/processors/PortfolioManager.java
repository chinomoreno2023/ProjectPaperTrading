package options.papertrading.services.processors;

import jakarta.persistence.EntityManager;
import kafka.producer.model.dto.TradeCreatedEventDto;
import kafka.producer.service.ProducerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.PortfolioConverter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioManager implements IPortfolioManager {
    private final IPersonFacadeHtmlVersion personsService;
    private final JournalRepository journalRepository;
    private final PortfoliosRepository portfoliosRepository;
    private final EntityManager entityManager;
    private final PortfolioConverter portfolioConverter;
    private final IPositionSetter positionSetter;
    private final IJournalFacade journalService;
    private final ProducerService producerService;

    public Portfolio checkOrderTime(OptionDto optionDto) {
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

        Portfolio portfolio = portfolioConverter.convertOptionDtoToPortfolio(optionDto);
        LocalTime expiryCheckTime = LocalTime.of(14, 0);
        if (!currentTimeInMoscow.isBefore(expiryCheckTime) && portfolio.getOption().getDaysToMaturity() <= 0) {
            throw new IllegalArgumentException("Дата исполнения опциона истекла и в ближайшее время он будет удалён");
        }
        return portfolio;
    }

    @Transactional
    @Override
    public void reset() {
        Person owner = personsService.getCurrentPerson();
        journalRepository.deleteAllByOwner(owner);
        portfoliosRepository.deleteAllByOwner(owner);
        owner.setCurrentNetPosition(0);
        owner.setOpenLimit(1000000);
    }

    @Scheduled(cron = "0 00 14 * * MON-FRI")
    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 10000))
    @Transactional
    @Override
    public void expirationCheck() {
        List<Portfolio> portfolios = portfoliosRepository.findAll();
        if (!portfolios.isEmpty()) {
            portfolios.forEach(portfolio -> {
                if (portfolio.getOption() != null) {
                    entityManager.refresh(portfolio.getOption());
                } else {
                    log.error("Option does not exist for portfolio {}", portfolio);
                    portfoliosRepository.delete(portfolio);
                    return;
                }
                OptionDto optionDto = portfolioConverter.convertPortfolioToOptionDto(portfolio);

                if (portfolio.getOption().getDaysToMaturity() <= 0) {
                    double savedVariatMargin = portfolio.getVariatMargin();
                    if (portfolio.getVolume() < 0) {
                        portfolio.setVariatMargin(savedVariatMargin + (updateVariatMargin(portfolio) * (-1)));
                        portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                                + portfolio.getCollateralWhenWasTrade() * Math.abs(portfolio.getVolume()));
                        optionDto.setBuyOrWrite(1);
                    }
                    if (portfolio.getVolume() > 0) {
                        portfolio.setVariatMargin(updateVariatMargin(portfolio) + savedVariatMargin);
                        portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit()
                                + portfolio.getCollateralWhenWasTrade() * portfolio.getVolume());
                        optionDto.setBuyOrWrite(-1);
                    }

                    journalService.addJournal(portfolio, optionDto);
                    portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit() + portfolio.getVariatMargin());
                    personsService.save(portfolio.getOwner());
                    log.info("Portfolio {} will be deleted", portfolio);

                    producerService.createEvent(new TradeCreatedEventDto(portfolio, null));
                }
            });
        }
    }

    @Transactional
    @Override
    public void updateCurrentNetPositionAndOpenLimit() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        double savedNetPosition = owner.getCurrentNetPosition();
        owner.setCurrentNetPosition(0);

        if (!optionsInPortfolio.isEmpty()) {
            optionsInPortfolio.forEach(portfolio -> {
                if (portfolio.getOption() != null) {
                    entityManager.refresh(portfolio.getOption());
                } else {
                    log.error("Option does not exist for portfolio {}", portfolio);
                    portfoliosRepository.delete(portfolio);
                    return;
                }
                if (portfolio.getVolume() > 0) {
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getBuyCollateral()
                            * portfolio.getVolume());
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getBuyCollateral());
                }
                if (portfolio.getVolume() < 0) {
                    owner.setCurrentNetPosition(owner.getCurrentNetPosition() + portfolio.getOption().getWriteCollateral()
                            * Math.abs(portfolio.getVolume()));
                    portfolio.setCollateralWhenWasTrade(portfolio.getOption().getWriteCollateral());
                }
            });

            owner.setOpenLimit(owner.getOpenLimit() - (owner.getCurrentNetPosition() - savedNetPosition));
            owner.setOptionsInPortfolio(optionsInPortfolio);
            log.info("Refreshed portfolios: {}", optionsInPortfolio);
            personsService.save(owner);
        }
    }

    @Scheduled(cron = "0 05 14 * * MON-FRI")
    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 10000))
    @Transactional
    @Override
    public void doClearing() {
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAll();
        if (!optionsInPortfolio.isEmpty()) {
            optionsInPortfolio.forEach(portfolio -> {
                if (portfolio.getOption() != null) {
                    entityManager.refresh(portfolio.getOption());
                } else {
                    log.error("Option does not exist for portfolio {}", portfolio);
                    portfoliosRepository.delete(portfolio);
                    return;
                }
                double savedVariatMargin = portfolio.getVariatMargin();
                if (portfolio.getVolume() < 0) {
                    portfolio.setVariatMargin(savedVariatMargin
                            + (updateVariatMargin(portfolio)
                            * (-1)));
                }
                if (portfolio.getVolume() > 0) {
                    portfolio.setVariatMargin(updateVariatMargin(portfolio)
                                    + savedVariatMargin);
                }
                portfolio.setTradePrice(positionSetter.countPriceInRur(portfolio));
                portfolio.getOwner().setOpenLimit(portfolio.getOwner().getOpenLimit() + portfolio.getVariatMargin());
                portfolio.setVariatMargin(0);
                personsService.save(portfolio.getOwner());

                if (portfolio.getVolume() == 0) {
                    log.info("Portfolio {} will be deleted", portfolio);
                    portfoliosRepository.delete(portfolio);
                } else {
                    log.info("Refreshed portfolio: {}", portfolio);
                    portfoliosRepository.save(portfolio);
                }
            });
        }
    }

    @Transactional
    @Scheduled(cron = "0 00 19 * * MON-FRI")
    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 10000))
    public void doEveningClearing() {
        doClearing();
    }

    @Transactional
    @Override
    public void updateAllVariatMargin() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        if (!optionsInPortfolio.isEmpty()) {
            optionsInPortfolio
                    .stream()
                    .filter(portfolio -> portfolio.getVolume() != 0)
                    .forEach(portfolio -> {
                        if (portfolio.getOption() != null) {
                            entityManager.refresh(portfolio.getOption());
                        } else {
                            log.error("Option does not exist for portfolio {}", portfolio);
                            portfoliosRepository.delete(portfolio);
                            return;
                        }
                        double savedVariatMargin = portfolio.getVariatMargin();
                        if (portfolio.getVolume() < 0) {
                            portfolio.setVariatMargin(savedVariatMargin
                                    + (updateVariatMargin(portfolio)
                                    * (-1)));
                        } else {
                            portfolio.setVariatMargin(updateVariatMargin(portfolio)
                                            + savedVariatMargin);
                        }
                        portfolio.setTradePrice(positionSetter.countPriceInRur(portfolio));
                    });
            owner.setOptionsInPortfolio(optionsInPortfolio);
            log.info("Refreshed portfolios: {}", optionsInPortfolio);
            personsService.save(owner);
        }
    }

    @Transactional
    @Override
    public double updateVariatMargin(@NonNull Portfolio portfolio) {
        portfolio.setVariatMargin(positionSetter.countPriceInRur(portfolio)
                * Math.abs(portfolio.getVolume())
                - (portfolio.getTradePrice() * Math.abs(portfolio.getVolume())));
        double updatedVariatMargin = portfolio.getVariatMargin();
        log.info("Updated variat margin is {}", updatedVariatMargin);
        return updatedVariatMargin;
    }
}
