package options.papertrading.util.converters;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.option.Option;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.mappers.PortfolioMapper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class PortfolioConverter {
    private final PortfolioMapper portfolioMapper;
    private final IPositionSetter positionSetter;
    private final OptionsRepository optionsRepository;
    private final IPersonFacadeHtmlVersion personsService;

    public PortfolioDto convertToPortfolioDto(@NonNull Portfolio portfolio) {
        PortfolioDto portfolioDto = portfolioMapper.convertToPortfolioDto(portfolio);
        portfolioDto.setPrice(positionSetter.countPriceInRur(portfolio));
        log.info("Converting {} to portfolioDto. Result: {}", portfolio, portfolioDto);
        return portfolioDto;
    }

    public List<PortfolioDto> convertListToPortfolioDtoList(@NonNull List<Portfolio> portfolios) {
        List<PortfolioDto> portfolioDtoList = portfolios.parallelStream()
                .map(this::convertToPortfolioDto)
                .collect(Collectors.toList());
        log.info("Converting portfolio list '{}' to portfolioDto list. Result: {} ", portfolios, portfolioDtoList);
        return portfolioDtoList;
    }

    public Portfolio convertOptionDtoToPortfolio (@NonNull OptionDto optionDto) {
        Option option = optionsRepository.findOneById(optionDto.getId());
        Portfolio portfolio = new Portfolio();
        portfolio.setVolume(optionDto.getVolume());
        portfolio.setOwner(personsService.getCurrentPerson());
        portfolio.setOption(option);
        log.info("Converting optionDto '{}' to portfolio. Result: {}", optionDto, portfolio);
        return portfolio;
    }

    public OptionDto convertPortfolioToOptionDto(@NonNull Portfolio portfolio) {
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
}
