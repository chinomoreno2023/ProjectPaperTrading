package options.papertrading.util.mappers;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    OptionMapper INSTANCE = Mappers.getMapper(OptionMapper.class);
    OptionDto convertToOptionDto(Option option);
}
