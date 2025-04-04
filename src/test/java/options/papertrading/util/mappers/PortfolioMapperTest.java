package options.papertrading.util.mappers;

import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioMapperTest {
    private final PortfolioMapper portfolioMapper = Mappers.getMapper(PortfolioMapper.class);

    @Test
    void convertToPortfolioDto() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setVolume(10);
        portfolio.setTradePrice(100.0);
        portfolio.setVolatilityWhenWasTrade(0.1);
        portfolio.setVariatMargin(0.2);
        portfolio.setCollateralWhenWasTrade(0.3);

        Option option = new Option();
        option.setId("optionId");
        option.setStrike(7);
        option.setType("Call");
        option.setDaysToMaturity(5);
        option.setVolatility(7.6);
        option.setBuyCollateral(5.5);
        option.setWriteCollateral(6.6);
        option.setStepPrice(0.1);
        portfolio.setOption(option);

        Person person = new Person();
        person.setOpenLimit(666.6);
        person.setCurrentNetPosition(777.7);
        portfolio.setOwner(person);

        PortfolioDto portfolioDto = portfolioMapper.convertToPortfolioDto(portfolio);

        assertAll(
                () -> assertNotNull(portfolioDto),
                () -> assertEquals("optionId", portfolioDto.getId()),
                () -> assertEquals(7, portfolioDto.getStrike()),
                () -> assertEquals("Call", portfolioDto.getType()),
                () -> assertEquals(10, portfolioDto.getVolume()),
                () -> assertEquals(5, portfolioDto.getDaysToMaturity()),
                () -> assertEquals(100.0, portfolioDto.getTradePrice(), 0.001),
                () -> assertEquals(0.0, portfolioDto.getPrice(), 0.001),
                () -> assertEquals(7.6, portfolioDto.getVolatility(), 0.001),
                () -> assertEquals(0.3, portfolioDto.getCollateralWhenWasTrade(), 0.001),
                () -> assertEquals(5.5, portfolioDto.getBuyCollateral(), 0.001),
                () -> assertEquals(6.6, portfolioDto.getWriteCollateral(), 0.001),
                () -> assertEquals(0.2, portfolioDto.getVariatMargin(), 0.001),
                () -> assertEquals(777.7, portfolioDto.getCurrentNetPosition(), 0.001),
                () -> assertEquals(666.6, portfolioDto.getOpenLimit(), 0.001),
                () -> assertEquals(0.1, portfolioDto.getStepPrice(), 0.001)
        );
    }

    @Test
    void convertToPortfolioDto_shouldReturnNullAndDoesNotThrow_whenInputIsNull() {
        Portfolio portfolio = null;

        PortfolioDto portfolioDto = portfolioMapper.convertToPortfolioDto(portfolio);

        assertNull(portfolioDto);
    }
}
