package options.papertrading.services.processors;

import jakarta.persistence.EntityManager;
import kafka.producer.service.ProducerService;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.PortfolioConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioManagerTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private PortfoliosRepository portfoliosRepository;

    @Mock
    private IPersonFacadeHtmlVersion personsService;

    @Mock
    private PortfolioConverter portfolioConverter;

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private EntityManager entityManager;

    @Mock
    private IJournalFacade journalService;

    @Mock
    private ProducerService producerService;

    @InjectMocks
    private PortfolioManager portfolioManager;

    @Test
    void checkOrderTime_shouldThrowWhenOutsideWorkingHours() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Moscow"));
        if (now.getDayOfWeek().getValue() < 6
                && now.toLocalTime().isAfter(LocalTime.of(6, 50))
                && now.toLocalTime().isBefore(LocalTime.of(23, 50))) {
            return;
        }

        assertThrows(IllegalArgumentException.class,
                () -> portfolioManager.checkOrderTime(new OptionDto()));
    }

    @Test
    void reset() {
        Person owner = new Person();
        when(personsService.getCurrentPerson()).thenReturn(owner);
        owner.setCurrentNetPosition(0);
        owner.setOpenLimit(1000000);

        portfolioManager.reset();

        assertAll(
                () -> verify(personsService, times(1)).getCurrentPerson(),
                () -> verify(journalRepository, times(1)).deleteAllByOwner(owner),
                () -> verify(portfoliosRepository, times(1)).deleteAllByOwner(owner),
                () -> assertEquals(0, owner.getCurrentNetPosition()),
                () -> assertEquals(1_000_000, owner.getOpenLimit()),
                () -> verifyNoMoreInteractions(personsService, journalRepository, portfoliosRepository),
                () -> assertTrue(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("reset"),
                        Transactional.class) != null)
        );
    }

    @Test
    void expirationCheck_shouldCorrectlyUpdateVariationMargin() {
        Portfolio shortPortfolio = createTestPortfolio(0, -3); // SHORT
        Portfolio longPortfolio = createTestPortfolio(0, 2);   // LONG

        when(portfoliosRepository.findAll()).thenReturn(List.of(shortPortfolio, longPortfolio));
        when(portfolioConverter.convertPortfolioToOptionDto(any())).thenReturn(new OptionDto());

        PortfolioManager spyManager = spy(portfolioManager);
        doReturn(100.0).when(spyManager).updateVariatMargin(any()); // Всегда возвращаем 100

        spyManager.expirationCheck();

        assertAll(
                // SHORT: saved(1000) + [new(100) * (-1)] = 900
                () -> assertEquals(900.0, shortPortfolio.getVariatMargin(), 0.01),

                // LONG: new(100) + saved(1000) = 1100
                () -> assertEquals(1100.0, longPortfolio.getVariatMargin(), 0.01),

                () -> verify(spyManager, times(2)).updateVariatMargin(any()),
                () -> verify(journalService).addJournal(eq(shortPortfolio), any()),
                () -> verify(journalService).addJournal(eq(longPortfolio), any())
        );
    }

    @Test
    void expirationCheck_shouldHandleMissingOption() {
        Portfolio portfolio = new Portfolio();
        portfolio.setOption(null);
        portfolio.setOwner(new Person());

        when(portfoliosRepository.findAll()).thenReturn(List.of(portfolio));

        portfolioManager.expirationCheck();

        assertAll(
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("expirationCheck"),
                        Scheduled.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("expirationCheck"),
                        Retryable.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("expirationCheck"),
                        Transactional.class)),
                () -> verify(portfoliosRepository).delete(portfolio),
                () -> verifyNoInteractions(
                        journalService,
                        personsService,
                        producerService,
                        entityManager
                )
        );
    }

    private Portfolio createTestPortfolio(int daysToMaturity, int volume) {
        Portfolio portfolio = new Portfolio();
        Option option = new Option();
        option.setDaysToMaturity(daysToMaturity);
        portfolio.setOption(option);
        portfolio.setVolume(volume);
        portfolio.setVariatMargin(1000.0);
        portfolio.setCollateralWhenWasTrade(500.0);

        Person owner = new Person();
        owner.setOpenLimit(10_000);
        portfolio.setOwner(owner);

        return portfolio;
    }

    @Test
    void updateCurrentNetPositionAndOpenLimit_shouldUpdateValuesCorrectly() {
        // Arrange
        Person owner = new Person();
        owner.setCurrentNetPosition(5000.0);
        owner.setOpenLimit(100_000.0);

        Portfolio longPortfolio = createTestPortfolio(100.0, 3);  // buyCollateral = 100
        Portfolio shortPortfolio = createTestPortfolio(100.0, -2); // writeCollateral = 150 (1.5 * buy)

        when(personsService.getCurrentPerson()).thenReturn(owner);
        when(portfoliosRepository.findAllByOwner(owner)).thenReturn(List.of(longPortfolio, shortPortfolio));

        // Act
        portfolioManager.updateCurrentNetPositionAndOpenLimit();

        // Assert
        assertAll(
                // LONG: buyCollateral = 100
                () -> assertEquals(100.0, longPortfolio.getCollateralWhenWasTrade()),

                // SHORT: writeCollateral = 150 (1.5 * buy)
                () -> assertEquals(150.0, shortPortfolio.getCollateralWhenWasTrade()),

                // NetPosition: (100 * 3) + (150 * 2) = 600
                () -> assertEquals(600.0, owner.getCurrentNetPosition(), 0.01),

                // OpenLimit: 100_000 - (600 - 5000) = 100_000 + 4400 = 104_400
                () -> assertEquals(104_400.0, owner.getOpenLimit(), 0.01),

                () -> verify(personsService).save(owner)
        );
    }

    private Portfolio createTestPortfolio(double buyCollateral, int volume) {
        Portfolio portfolio = new Portfolio();
        Option option = new Option();
        option.setBuyCollateral(buyCollateral);
        option.setWriteCollateral(buyCollateral * 1.5); // Фиксированное соотношение
        portfolio.setOption(option);
        portfolio.setVolume(volume);
        return portfolio;
    }

    @Test
    void updateCurrentNetPositionAndOpenLimit_shouldHandleEmptyPortfolio() {
        // Arrange
        Person owner = new Person();
        owner.setCurrentNetPosition(1000.0);
        owner.setOpenLimit(50_000.0);

        when(personsService.getCurrentPerson()).thenReturn(owner);
        when(portfoliosRepository.findAllByOwner(owner)).thenReturn(Collections.emptyList());

        // Act
        portfolioManager.updateCurrentNetPositionAndOpenLimit();

        // Assert
        assertAll(
                () -> assertEquals(0.0, owner.getCurrentNetPosition(), 0.01,
                        "CurrentNetPosition должен быть сброшен в 0"),

                () -> assertEquals(50_000.0, owner.getOpenLimit(), 0.01,
                        "OpenLimit не должен измениться при пустом портфеле"),

                () -> verify(personsService, never()).save(any()),
                () -> verifyNoMoreInteractions(personsService),
                () -> verifyNoInteractions(entityManager),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("updateCurrentNetPositionAndOpenLimit"),
                        Transactional.class))
        );
    }

    @Test
    void doClearing_shouldDeletePortfolio_whenOptionIsNull() {
        Portfolio portfolio = mock(Portfolio.class);
        when(portfolio.getOption()).thenReturn(null);
        when(portfoliosRepository.findAll()).thenReturn(List.of(portfolio));

        portfolioManager.doClearing();

        verify(portfoliosRepository).delete(portfolio);
    }

    @Test
    void doClearing_shouldUpdateVariatMargin_whenVolumeNegative() {
        Portfolio portfolio = mock(Portfolio.class);
        Person owner = mock(Person.class);

        when(portfolio.getOwner()).thenReturn(owner);
        when(portfolio.getOption()).thenReturn(mock(Option.class));
        when(portfolio.getVolume()).thenReturn(-1);
        when(portfolio.getVariatMargin()).thenReturn(100.0);
        when(positionSetter.countPriceInRur(portfolio)).thenReturn(50.0);
        when(portfoliosRepository.findAll()).thenReturn(List.of(portfolio));

        portfolioManager.doClearing();

        assertAll(
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("expirationCheck"),
                        Scheduled.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("expirationCheck"),
                        Retryable.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("expirationCheck"),
                        Transactional.class)),
                () -> verify(portfolio, atLeastOnce()).setVariatMargin(anyDouble()),
                () -> verify(portfoliosRepository).save(portfolio)
        );
    }

    @Test
    void doClearing_shouldDeletePortfolio_whenVolumeIsZero() {
        Portfolio portfolio = mock(Portfolio.class);
        Person owner = mock(Person.class);

        when(portfolio.getOption()).thenReturn(mock(Option.class));
        when(portfolio.getVolume()).thenReturn(0);
        when(portfolio.getOwner()).thenReturn(owner);
        when(owner.getOpenLimit()).thenReturn(1000.0);
        when(portfoliosRepository.findAll()).thenReturn(List.of(portfolio));

        portfolioManager.doClearing();

        verify(portfoliosRepository).delete(portfolio);
    }

    @Test
    void doClearing_shouldDoNothingWhenEmptyPortfolio() {
        when(portfoliosRepository.findAll()).thenReturn(Collections.emptyList());

        portfolioManager.doClearing();

        verifyNoInteractions(
                entityManager,
                positionSetter,
                personsService
        );
    }

    @Test
    void doEveningClearing() {
        PortfolioManager spyPortfolioManager = spy(portfolioManager);
        doNothing().when(spyPortfolioManager).doClearing();

        spyPortfolioManager.doEveningClearing();

        assertAll(
                () -> verify(spyPortfolioManager).doClearing(),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("doEveningClearing"),
                        Scheduled.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("doEveningClearing"),
                        Retryable.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("doEveningClearing"),
                        Transactional.class))
        );
    }

    @Test
    void updateAllVariatMargin_shouldUpdateAllPositionsCorrectly() {
        Person owner = new Person();
        owner.setOpenLimit(50_000.0);

        Portfolio longPortfolio = createTestPortfolio(3, 1000.0, owner);
        Portfolio shortPortfolio = createTestPortfolio(-2, 1500.0, owner);
        Portfolio zeroVolumePortfolio = createTestPortfolio(0, 500.0, owner);

        when(personsService.getCurrentPerson()).thenReturn(owner);
        when(portfoliosRepository.findAllByOwner(owner)).thenReturn(List.of(
                longPortfolio,
                shortPortfolio,
                zeroVolumePortfolio
        ));
        when(positionSetter.countPriceInRur(any(Portfolio.class))).thenReturn(250.0);

        PortfolioManager spyManager = spy(portfolioManager);
        doReturn(200.0).when(spyManager).updateVariatMargin(any());

        spyManager.updateAllVariatMargin();

        assertAll(
                () -> assertEquals(1200.0, longPortfolio.getVariatMargin(), 0.01,
                        "LONG: 200 (new) + 1000 (saved) = 1200"),
                () -> assertEquals(250.0, longPortfolio.getTradePrice(), 0.01,
                        "TradePrice должен быть установлен"),

                () -> assertEquals(1300.0, shortPortfolio.getVariatMargin(), 0.01,
                        "SHORT: 1500 + (200 * -1) = 1300"),
                () -> assertEquals(250.0, shortPortfolio.getTradePrice(), 0.01,
                        "TradePrice должен быть установлен"),

                () -> assertEquals(500.0, zeroVolumePortfolio.getVariatMargin(), 0.01,
                        "Zero-volume позиция не должна изменяться"),

                () -> verify(entityManager).refresh(longPortfolio.getOption()),
                () -> verify(entityManager).refresh(shortPortfolio.getOption()),
                () -> verify(entityManager, never()).refresh(zeroVolumePortfolio.getOption()),

                () -> verify(positionSetter, times(2)).countPriceInRur(any(Portfolio.class)),
                () -> verify(personsService).save(owner),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("doEveningClearing"),
                        Transactional.class))
        );
    }

    @Test
    void updateAllVariatMargin_shouldDeletePortfolio_whenOptionIsNull() {
        Person owner = mock(Person.class);
        when(personsService.getCurrentPerson()).thenReturn(owner);

        Portfolio portfolio = mock(Portfolio.class);
        when(portfolio.getOption()).thenReturn(null); // option == null
        when(portfolio.getVolume()).thenReturn(1); // Должен пройти через filter
        when(portfoliosRepository.findAllByOwner(owner)).thenReturn(List.of(portfolio));

        portfolioManager.updateAllVariatMargin();

        assertAll(
                () -> verify(portfoliosRepository).delete(portfolio),
                () -> verifyNoMoreInteractions(portfolio)
        );
    }

    private Portfolio createTestPortfolio(int volume, double variatMargin, Person owner) {
        Portfolio portfolio = new Portfolio();
        portfolio.setOption(new Option());
        portfolio.setVolume(volume);
        portfolio.setVariatMargin(variatMargin);
        portfolio.setOwner(owner);
        return portfolio;
    }

    @Test
    void updateAllVariatMargin_shouldNotUpdateAnything_whenNoPortfolios() {
        Person mockPerson = mock(Person.class);

        when(personsService.getCurrentPerson()).thenReturn(mockPerson);
        when(portfoliosRepository.findAllByOwner(mockPerson)).thenReturn(Collections.emptyList());

        portfolioManager.updateAllVariatMargin();

        assertAll(
                () -> verify(entityManager, never()).refresh(any()),
                () -> verify(personsService, never()).save(any()),
                () -> verify(portfoliosRepository, never()).delete(any()),
                () -> verify(mockPerson, never()).setOptionsInPortfolio(any())
        );
    }

    @Test
    void updateVariatMargin() {
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(-10);
        portfolio.setTradePrice(800.0);

        when(positionSetter.countPriceInRur(any(Portfolio.class))).thenReturn(1000.0);

        portfolioManager.updateVariatMargin(portfolio);

        assertAll(
                () -> assertEquals(2000.0, portfolio.getVariatMargin(), 0.001),
                () -> assertEquals(800.0, portfolio.getTradePrice(), 0.001),
                () -> assertEquals(-10, portfolio.getVolume()),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        PortfolioManager.class.getMethod("doEveningClearing"),
                        Transactional.class))
        );
    }

    @Test
    void updateVariatMargin_ShouldNotChangeVariatMargin_WhenVolumeIsZero() {
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(0);
        portfolio.setTradePrice(800.0);

        when(positionSetter.countPriceInRur(any(Portfolio.class))).thenReturn(1000.0);

        double result = portfolioManager.updateVariatMargin(portfolio);

        assertAll(
                () -> assertEquals(0.0, portfolio.getVariatMargin(), 0.001),
                () -> assertEquals(0.0, result, 0.001),
                () -> assertEquals(800.0, portfolio.getTradePrice(), 0.001),
                () -> assertEquals(0, portfolio.getVolume())
        );
    }


    @Test
    void updateVariatMargin_ShouldThrows_whenInputIsNull() {
        Portfolio portfolio = null;

        assertThrows(NullPointerException.class,
                () -> portfolioManager.updateVariatMargin(portfolio));
    }
}