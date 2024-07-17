package options.papertrading.util.mappers;

import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.portfolio.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    PortfolioMapper INSTANCE = Mappers.getMapper(PortfolioMapper.class);

    @Mapping(target = "openLimit", source = "portfolio.owner.openLimit")
    @Mapping(target = "id", source = "portfolio.option.id")
    @Mapping(target = "strike", source = "portfolio.option.strike")
    @Mapping(target = "type", source = "portfolio.option.type")
    @Mapping(target = "daysToMaturity", source = "portfolio.option.daysToMaturity")
    @Mapping(target = "volatility", source = "portfolio.option.volatility")
    @Mapping(target = "buyCollateral", source = "portfolio.option.buyCollateral")
    @Mapping(target = "writeCollateral", source = "portfolio.option.writeCollateral")
    @Mapping(target = "currentNetPosition", source = "portfolio.owner.currentNetPosition")
    @Mapping(target = "stepPrice", source = "portfolio.option.stepPrice")
    PortfolioDto convertToPortfolioDto(Portfolio portfolio);
}