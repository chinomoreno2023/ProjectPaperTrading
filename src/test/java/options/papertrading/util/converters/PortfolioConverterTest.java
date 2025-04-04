package options.papertrading.util.converters;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.mappers.PortfolioMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioConverterTest {

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private PortfolioMapper portfolioMapper;

    @Mock
    private OptionsRepository optionsRepository;

    @Mock
    private IPersonFacadeHtmlVersion personsService;

    @InjectMocks
    private PortfolioConverter portfolioConverter;

    @Test
    void convertToPortfolioDto() {
        Portfolio portfolio = new Portfolio();
        portfolio.setOption(new Option());
        portfolio.setOwner(new Person());
        PortfolioDto portfolioDto = new PortfolioDto();
        double expectedPrice = 7.0;
        when(portfolioMapper.convertToPortfolioDto(portfolio)).thenReturn(portfolioDto);
        when(positionSetter.countPriceInRur(portfolio)).thenReturn(expectedPrice);

        PortfolioDto result = portfolioConverter.convertToPortfolioDto(portfolio);

        assertAll(
                () -> verify(portfolioMapper).convertToPortfolioDto(portfolio),
                () -> verify(positionSetter).countPriceInRur(portfolio),
                () -> assertEquals(portfolioDto, result),
                () -> assertEquals(expectedPrice, result.getPrice(), 0.001)
        );
    }

    @Test
    void convertToPortfolioDto_shouldThrow_whenInputIsNull() {
        Portfolio portfolio = null;

        assertThrows(NullPointerException.class,
                        () -> portfolioConverter.convertToPortfolioDto(portfolio));
    }

    @Test
    void convertListToPortfolioDtoList() {
        int size = ThreadLocalRandom.current().nextInt(3, 11);
        List<Portfolio> portfolios = IntStream.range(0, size)
                .mapToObj(i -> {
                    Portfolio p = new Portfolio();
                    p.setOption(new Option());
                    p.setOwner(new Person());
                    return p;
                })
                .collect(toList());

        when(portfolioMapper.convertToPortfolioDto(any())).thenReturn(new PortfolioDto());
        when(positionSetter.countPriceInRur(any(Portfolio.class))).thenReturn(7.0);


        List<PortfolioDto> result = portfolioConverter.convertListToPortfolioDtoList(portfolios);

        assertAll(
                () -> verify(portfolioMapper, times(size)).convertToPortfolioDto(any()),
                () -> verify(positionSetter, times(size)).countPriceInRur(any(Portfolio.class)),
                () -> assertNotNull(result),
                () -> assertFalse(result.contains(null)),
                () -> assertEquals(size, result.size()),
                () -> assertTrue(result.stream().allMatch(dto -> dto.getPrice() == 7.0))
        );
    }

    @Test
    void convertListToPortfolioDtoList_shouldThrow_whenInputIsNull() {
        List<Portfolio> portfolios = null;

        assertThrows(NullPointerException.class,
                        () -> portfolioConverter.convertListToPortfolioDtoList(portfolios));
    }

    @Test
    void convertOptionDtoToPortfolio() {
        OptionDto optionDto = new OptionDto();
        optionDto.setId("1");
        optionDto.setVolume(5);

        Option option = new Option();
        Person person = new Person();

        when(optionsRepository.findOneById(optionDto.getId())).thenReturn(option);
        when(personsService.getCurrentPerson()).thenReturn(person);

        Portfolio result = portfolioConverter.convertOptionDtoToPortfolio(optionDto);

        assertAll(
                () -> verify(optionsRepository).findOneById(optionDto.getId()),
                () -> verify(personsService).getCurrentPerson(),
                () -> assertNotNull(result),
                () -> assertEquals(option, result.getOption()),
                () -> assertEquals(person, result.getOwner()),
                () -> assertEquals(5, result.getVolume())
        );
    }

    @Test
    void convertOptionDtoToPortfolio_shouldThrow_whenInputIsNull() {
        OptionDto optionDto = null;
        assertThrows(NullPointerException.class,
                        () -> portfolioConverter.convertOptionDtoToPortfolio(optionDto));
    }


    @Test
    void convertPortfolioToOptionDto() {
        Portfolio portfolio = new Portfolio();
        Person person = new Person();
        portfolio.setOwner(person);
        Option option = new Option();
        option.setId("1");
        option.setStrike(10);
        option.setType("Call");
        option.setPrice(40.0);
        option.setVolatility(666.0);
        option.setDaysToMaturity(2);
        option.setBuyCollateral(101.0);
        option.setWriteCollateral(202.0);
        portfolio.setVolume(8);
        portfolio.setOption(option);

        OptionDto result = portfolioConverter.convertPortfolioToOptionDto(portfolio);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("1", result.getId()),
                () -> assertEquals(10, result.getStrike()),
                () -> assertEquals("Call", result.getType()),
                () -> assertEquals(40.0, result.getPrice(), 0.001),
                () -> assertEquals(666.0, result.getVolatility(), 0.001),
                () -> assertEquals(2, result.getDaysToMaturity()),
                () -> assertEquals(101.0, result.getBuyCollateral(), 0.001),
                () -> assertEquals(202.0, result.getWriteCollateral(), 0.001),
                () -> assertEquals(0, result.getStepPrice(), 0.001),
                () -> assertEquals(8, result.getVolume()),
                () -> assertEquals(0, result.getBuyOrWrite())
        );
    }

    @Test
    void convertPortfolioToOptionDto_shouldThrow_whenInputIsNull() {
        Portfolio portfolio = null;

        assertThrows(NullPointerException.class,
                        () -> portfolioConverter.convertPortfolioToOptionDto(portfolio));
    }
}