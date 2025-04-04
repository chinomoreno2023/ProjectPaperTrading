package options.papertrading.services.processors;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionSetterTest {

    @Mock
    private PortfoliosRepository portfoliosRepository;

    @InjectMocks
    private PositionSetter positionSetter;

    @Test
    void isThereEnoughMoneyForBuy_shouldReturnTrueWhenEnoughMoney() throws NoSuchMethodException {
        Portfolio portfolio = createTestPortfolioForBuying(10000.0, 5000.0, 2);

        boolean result = positionSetter.isThereEnoughMoneyForBuy(portfolio);

        assertAll(
                () -> assertTrue(result),
                () -> verify(portfoliosRepository, never()).save(any()),
                () -> assertNotNull(PositionSetter.class
                                .getMethod("isThereEnoughMoneyForBuy", Portfolio.class)
                                .getAnnotation(Transactional.class))
        );
    }

    @Test
    void isThereEnoughMoneyForBuy_shouldReturnFalseWhenNotEnoughMoney() {
        Portfolio portfolio = createTestPortfolioForBuying(5000.0, 10000.0, 3);

        boolean result = positionSetter.isThereEnoughMoneyForBuy(portfolio);

        assertAll(
                () -> assertFalse(result),
                () -> verifyNoInteractions(portfoliosRepository)
        );
    }

    @Test
    void isThereEnoughMoneyForBuy_shouldHandleZeroVolume() {
        Portfolio portfolio = createTestPortfolioForBuying(10000.0, 5000.0, 0);

        boolean result = positionSetter.isThereEnoughMoneyForBuy(portfolio);

        assertTrue(result);
    }

    private Portfolio createTestPortfolioForBuying(double openLimit, double buyCollateral, int volume) {
        Person owner = new Person();
        owner.setOpenLimit(openLimit);

        Option option = new Option();
        option.setBuyCollateral(buyCollateral);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(owner);
        portfolio.setOption(option);
        portfolio.setVolume(volume);

        return portfolio;
    }

    @Test
    void isThereEnoughMoneyForBuy_ShouldThrowException_WhenPortfolioIsNull() {
        assertThrows(NullPointerException.class,
                () -> positionSetter.isThereEnoughMoneyForBuy(null));
    }

    @Test
    void isThereEnoughMoneyForWrite_shouldReturnTrueWhenEnoughMoney() throws NoSuchMethodException {
        Portfolio portfolio = createTestPortfolioForWriting(15000.0, 5000.0, -3);

        boolean result = positionSetter.isThereEnoughMoneyForWrite(portfolio);

        assertAll(
                () -> assertTrue(result),
                () -> verify(portfoliosRepository, never()).save(any()),
                () -> assertNotNull(PositionSetter.class
                        .getMethod("isThereEnoughMoneyForWrite", Portfolio.class)
                        .getAnnotation(Transactional.class))
        );
    }

    @Test
    void isThereEnoughMoneyForWrite_shouldReturnFalseWhenNotEnoughMoney() {
        Portfolio portfolio = createTestPortfolioForWriting(5000.0, 3000.0, -2);

        boolean result = positionSetter.isThereEnoughMoneyForWrite(portfolio);

        assertAll(
                () -> assertFalse(result),
                () -> verifyNoInteractions(portfoliosRepository)
        );
    }

    @Test
    void isThereEnoughMoneyForWrite_shouldHandleZeroVolume() {
        Portfolio portfolio = createTestPortfolioForWriting(10000.0, 5000.0, 0);

        boolean result = positionSetter.isThereEnoughMoneyForWrite(portfolio);

        assertTrue(result);
    }

    @Test
    void isThereEnoughMoneyForWrite_shouldUseAbsoluteVolume() {
        Portfolio portfolio = createTestPortfolioForWriting(10000.0, 3500.0, -3);

        boolean result = positionSetter.isThereEnoughMoneyForWrite(portfolio);

        assertFalse(result); // 10000 < (3500 * 3 = 10500)
    }

    private Portfolio createTestPortfolioForWriting(double openLimit, double writeCollateral, int volume) {
        Person owner = new Person();
        owner.setOpenLimit(openLimit);

        Option option = new Option();
        option.setWriteCollateral(writeCollateral);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(owner);
        portfolio.setOption(option);
        portfolio.setVolume(volume);

        return portfolio;
    }

    @Test
    void isThereEnoughMoneyForWrite_ShouldThrow_WhenPortfolioIsNull() {
        assertThrows(NullPointerException.class,
                () -> positionSetter.isThereEnoughMoneyForWrite(null));
    }

    @Test
    void isContained_shouldReturnTrueWhenPortfolioExists() {
        Portfolio portfolio = createTestPortfolioWithOption("111");
        Portfolio existingPortfolio = createTestPortfolioWithOption("111");

        when(portfoliosRepository.findAllByOwner(portfolio.getOwner()))
                .thenReturn(List.of(existingPortfolio));

        boolean result = positionSetter.isContained(portfolio);

        assertAll(
                () -> assertTrue(result),
                () -> verify(portfoliosRepository).findAllByOwner(portfolio.getOwner()),
                () -> assertNotNull(PositionSetter.class
                        .getMethod("isContained", Portfolio.class)
                        .getAnnotation(Transactional.class))
        );
    }

    @Test
    void isContained_shouldReturnFalseWhenPortfolioNotExists() {
        Portfolio portfolio = createTestPortfolioWithOption("111");
        Portfolio otherPortfolio = createTestPortfolioWithOption("222");

        when(portfoliosRepository.findAllByOwner(portfolio.getOwner()))
                .thenReturn(List.of(otherPortfolio));

        boolean result = positionSetter.isContained(portfolio);

        assertFalse(result);
    }

    @Test
    void isContained_shouldReturnFalseForEmptyList() {
        Portfolio portfolio = createTestPortfolioWithOption("111");

        when(portfoliosRepository.findAllByOwner(portfolio.getOwner()))
                .thenReturn(Collections.emptyList());

        boolean result = positionSetter.isContained(portfolio);

        assertFalse(result);
    }

    @Test
    void isContained_ShouldThrow_WhenPortfolioIsNull() {
        assertThrows(NullPointerException.class,
                () -> positionSetter.isContained(null));
    }

    private Portfolio createTestPortfolioWithOption(String optionId) {
        Option option = new Option();
        option.setId(optionId);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(new Person());
        portfolio.setOption(option);

        return portfolio;
    }

    @Test
    void checkBuyOrWrite_shouldReturnTrueForBuy() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(1);

        boolean result = positionSetter.checkBuyOrWrite(optionDto);

        assertTrue(result);
    }

    @Test
    void checkBuyOrWrite_shouldReturnFalseForWrite() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(-1);

        boolean result = positionSetter.checkBuyOrWrite(optionDto);

        assertFalse(result);
    }

    @Test
    void checkBuyOrWrite_shouldThrowForInvalidValue() {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(0);

        assertThrows(IllegalArgumentException.class,
                () -> positionSetter.checkBuyOrWrite(optionDto));
    }

    @Test
    void checkBuyOrWrite_shouldThrowForNullInput() {
        assertThrows(NullPointerException.class,
                () -> positionSetter.checkBuyOrWrite(null));
    }

    @Test
    void checkDirectOrReverse_shouldReturnTrueForBuyWithPositiveVolume() {
        OptionDto optionDto = createOptionDto(1);
        Portfolio portfolio = createPortfolio(5);

        boolean result = positionSetter.checkDirectOrReverse(optionDto, portfolio);

        assertTrue(result);
    }

    @Test
    void checkDirectOrReverse_shouldReturnFalseForBuyWithNegativeVolume() {
        OptionDto optionDto = createOptionDto(1);
        Portfolio portfolio = createPortfolio(-3);

        boolean result = positionSetter.checkDirectOrReverse(optionDto, portfolio);

        assertFalse(result);
    }

    @Test
    void checkDirectOrReverse_shouldReturnFalseForWriteWithPositiveVolume() {
        OptionDto optionDto = createOptionDto(-1);
        Portfolio portfolio = createPortfolio(2);

        boolean result = positionSetter.checkDirectOrReverse(optionDto, portfolio);

        assertFalse(result);
    }

    @Test
    void checkDirectOrReverse_shouldReturnTrueForWriteWithNegativeVolume() {
        OptionDto optionDto = createOptionDto(-1);
        Portfolio portfolio = createPortfolio(-4);

        boolean result = positionSetter.checkDirectOrReverse(optionDto, portfolio);

        assertTrue(result);
    }

    @Test
    void checkDirectOrReverse_shouldThrowForNullOptionDto() {
        Portfolio portfolio = createPortfolio(1);

        assertThrows(NullPointerException.class,
                () -> positionSetter.checkDirectOrReverse(null, portfolio));
    }

    @Test
    void checkDirectOrReverse_shouldThrowForNullPortfolio() {
        OptionDto optionDto = createOptionDto(1);

        assertThrows(NullPointerException.class,
                () -> positionSetter.checkDirectOrReverse(optionDto, null));
    }

    @Test
    void checkDirectOrReverse_shouldCheckTransactionalAnnotation() throws NoSuchMethodException {
        assertNotNull(PositionSetter.class
                .getMethod("checkDirectOrReverse", OptionDto.class, Portfolio.class)
                .getAnnotation(Transactional.class));
    }

    private OptionDto createOptionDto(int buyOrWrite) {
        OptionDto optionDto = new OptionDto();
        optionDto.setBuyOrWrite(buyOrWrite);
        return optionDto;
    }

    private Portfolio createPortfolio(int volume) {
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(volume);
        return portfolio;
    }

    @Test
    void isThereEnoughMoneyForReverseBuy_shouldReturnTrueWhenEnoughMoney() {
        Portfolio oldPortfolio = createTestPortfolio(10000.0, 2000.0, -5);
        Portfolio newPortfolio = createTestPortfolio(0.0, 3000.0, 3);

        boolean result = positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio);

        assertTrue(result); // (2000*5 + 10000) >= (3000*(3-5))
    }

    @Test
    void isThereEnoughMoneyForReverseBuy_shouldReturnFalseWhenNotEnoughMoney() {
        Portfolio oldPortfolio = createTestPortfolio(5000.0, 1000.0, -2);

        Portfolio newPortfolio = createTestPortfolio(0.0, 3000.0, 5);

        boolean result = positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio);

        assertFalse(result); // (1000*2 + 5000 = 7000) < (3000*(5 - 2) = 9000)
    }

    @Test
    void isThereEnoughMoneyForReverseBuy_shouldHandleZeroVolume() {
        Portfolio oldPortfolio = createTestPortfolio(10000.0, 2000.0, 0);
        Portfolio newPortfolio = createTestPortfolio(0.0, 3000.0, 3);

        boolean result = positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, newPortfolio);

        assertTrue(result); // (2000*0 + 10000) >= (3000*(3-0))
    }

    @Test
    void isThereEnoughMoneyForReverseBuy_shouldThrowForNullOldPortfolio() {
        Portfolio newPortfolio = createTestPortfolio(0.0, 3000.0, 3);

        assertThrows(NullPointerException.class,
                () -> positionSetter.isThereEnoughMoneyForReverseBuy(null, newPortfolio));
    }

    @Test
    void isThereEnoughMoneyForReverseBuy_shouldThrowForNullNewPortfolio() {
        Portfolio oldPortfolio = createTestPortfolio(10000.0, 2000.0, -5);

        assertThrows(NullPointerException.class,
                () -> positionSetter.isThereEnoughMoneyForReverseBuy(oldPortfolio, null));
    }

    @Test
    void isThereEnoughMoneyForReverseBuy_shouldCheckTransactionalAnnotation() throws NoSuchMethodException {
        assertNotNull(PositionSetter.class
                .getMethod("isThereEnoughMoneyForReverseBuy", Portfolio.class, Portfolio.class)
                .getAnnotation(Transactional.class));
    }

    private Portfolio createTestPortfolio(double openLimit, double collateral, int volume) {
        Person owner = new Person();
        owner.setOpenLimit(openLimit);

        Option option = new Option();
        option.setBuyCollateral(collateral);
        option.setWriteCollateral(collateral);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(owner);
        portfolio.setOption(option);
        portfolio.setVolume(volume);

        return portfolio;
    }

    @Test
    void isThereEnoughMoneyForReverseWrite_shouldReturnTrueWhenEnoughMoney() {
        Portfolio oldPortfolio = createTestPortfolioForReverseWriting(10000.0, 2000.0, 3);

        Portfolio newPortfolio = createTestPortfolioForReverseWriting(5000.0, 3000.0, -5);

        boolean result = positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio);

        assertTrue(result); // (10000 + 2000*3 = 16000) >= (3000*(5 - 3) = 6000)
    }

    @Test
    void isThereEnoughMoneyForReverseWrite_shouldReturnFalseWhenNotEnoughMoney() {
        Portfolio oldPortfolio = createTestPortfolioForReverseWriting(5000.0, 1000.0, 2);

        Portfolio newPortfolio = createTestPortfolioForReverseWriting(2000.0, 3000.0, -5);

        boolean result = positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio);

        assertFalse(result); // (5000 + 1000*2 = 7000) < (3000*(5 - 2) = 9000)
    }

    @Test
    void isThereEnoughMoneyForReverseWrite_shouldHandleZeroVolume() {
        Portfolio oldPortfolio = createTestPortfolio(10000.0, 2000.0, 0);

        Portfolio newPortfolio = createTestPortfolio(5000.0,3000.0,-3);

        boolean result = positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, newPortfolio);

        assertFalse(result);
    }

    @Test
    void isThereEnoughMoneyForReverseWrite_shouldThrowForNullOldPortfolio() {
        Portfolio newPortfolio = createTestPortfolioForReverseWriting(5000.0, 3000.0, -3);

        assertThrows(NullPointerException.class,
                () -> positionSetter.isThereEnoughMoneyForReverseWrite(null, newPortfolio));
    }

    @Test
    void isThereEnoughMoneyForReverseWrite_shouldThrowForNullNewPortfolio() {
        Portfolio oldPortfolio = createTestPortfolioForReverseWriting(10000.0, 2000.0, 3);

        assertThrows(NullPointerException.class,
                () -> positionSetter.isThereEnoughMoneyForReverseWrite(oldPortfolio, null));
    }

    @Test
    void isThereEnoughMoneyForReverseWrite_shouldCheckTransactionalAnnotation() throws NoSuchMethodException {
        assertNotNull(PositionSetter.class
                .getMethod("isThereEnoughMoneyForReverseWrite", Portfolio.class, Portfolio.class)
                .getAnnotation(Transactional.class));
    }

    private Portfolio createTestPortfolioForReverseWriting(double openLimit, double collateral, int volume) {
        Person owner = new Person();
        owner.setOpenLimit(openLimit);

        Option option = new Option();
        option.setBuyCollateral(collateral);
        option.setWriteCollateral(collateral);

        Portfolio portfolio = new Portfolio();
        portfolio.setOwner(owner);
        portfolio.setOption(option);
        portfolio.setVolume(volume);

        return portfolio;
    }

    @Test
    void countPriceInRur_shouldCalculateSFandBRcorrectly() {
        Portfolio portfolio = createTestPortfolio("SF123", 50.0, 10.0);
        double result = positionSetter.countPriceInRur(portfolio);
        assertEquals(50.0 * 10.0 * 100, result);
    }

    @Test
    void countPriceInRur_shouldCalculateSRcorrectly() {
        Portfolio portfolio = createTestPortfolio("SR456", 25.0, 5.0);
        double result = positionSetter.countPriceInRur(portfolio);
        assertEquals(25.0, result);
    }

    @Test
    void countPriceInRur_shouldCalculateRIcorrectly() {
        Portfolio portfolio = createTestPortfolio("RI789", 150.0, 2.0);
        double result = positionSetter.countPriceInRur(portfolio);
        assertEquals((150.0 / 10) * 2.0, result);
    }

    @Test
    void countPriceInRur_shouldReturnZeroForUnknownTicker() {
        Portfolio portfolio = createTestPortfolio("XX000", 100.0, 1.0);
        double result = positionSetter.countPriceInRur(portfolio);
        assertEquals(0.0, result);
    }

    @Test
    void countPriceInRur_shouldThrowForNullPortfolio() {
        Portfolio portfolio = null;
        assertThrows(NullPointerException.class,
                () -> positionSetter.countPriceInRur(portfolio));
    }

    @Test
    void countPriceInRur_shouldCheckTransactionalAnnotation() throws NoSuchMethodException {
        assertNotNull(PositionSetter.class
                .getMethod("countPriceInRur", Portfolio.class)
                .getAnnotation(Transactional.class));
    }

    private Portfolio createTestPortfolio(String optionId, double price, double stepPrice) {
        Option option = new Option();
        option.setId(optionId);
        option.setPrice(price);
        option.setStepPrice(stepPrice);

        Portfolio portfolio = new Portfolio();
        portfolio.setOption(option);
        return portfolio;
    }

    @Test
    void countPriceInRurOption_shouldCalculateSFandBRcorrectly() {
        Option option = createTestOptionForCountPriceInRurOption("SF123", 50.0, 10.0);
        double result = positionSetter.countPriceInRur(option);
        assertEquals(50.0 * 10.0 * 100, result);
    }

    @Test
    void countPriceInRurOption_shouldCalculateSRcorrectly() {
        Option option = createTestOptionForCountPriceInRurOption("SR456", 25.0, 5.0);
        double result = positionSetter.countPriceInRur(option);
        assertEquals(25.0, result);
    }

    @Test
    void countPriceInRurOption_shouldCalculateSiCorrectly() {
        Option option = createTestOptionForCountPriceInRurOption("Si789", 30.0, 1.0);
        double result = positionSetter.countPriceInRur(option);
        assertEquals(30.0, result);
    }

    @Test
    void countPriceInRurOption_shouldCalculateMXcorrectly() {
        Option option = createTestOptionForCountPriceInRurOption("MX456", 40.0, 2.0);
        double result = positionSetter.countPriceInRur(option);
        assertEquals(40.0, result);
    }

    @Test
    void countPriceInRurOption_shouldCalculateGZcorrectly() {
        Option option = createTestOptionForCountPriceInRurOption("GZ789", 35.0, 1.5);
        double result = positionSetter.countPriceInRur(option);
        assertEquals(35.0, result);
    }

    @Test
    void countPriceInRurOption_shouldCalculateRIcorrectly() {
        Option option = createTestOptionForCountPriceInRurOption("RI789", 150.0, 2.0);
        double result = positionSetter.countPriceInRur(option);
        assertEquals((150.0 / 10) * 2.0, result);
    }

    @Test
    void countPriceInRurOption_shouldReturnZeroForUnknownTicker() {
        Option option = createTestOptionForCountPriceInRurOption("XX000", 100.0, 1.0);
        double result = positionSetter.countPriceInRur(option);
        assertEquals(0.0, result);
    }

    @Test
    void countPriceInRurOption_shouldThrowForNullOption() {
        Option option = null;
        assertThrows(NullPointerException.class,
                () -> positionSetter.countPriceInRur(option));
    }

    @Test
    void countPriceInRurOption_shouldCheckTransactionalAnnotation() throws NoSuchMethodException {
        assertNotNull(PositionSetter.class
                .getMethod("countPriceInRur", Option.class)
                .getAnnotation(Transactional.class));
    }

    private Option createTestOptionForCountPriceInRurOption(String id, double price, double stepPrice) {
        Option option = new Option();
        option.setId(id);
        option.setPrice(price);
        option.setStepPrice(stepPrice);
        return option;
    }
}