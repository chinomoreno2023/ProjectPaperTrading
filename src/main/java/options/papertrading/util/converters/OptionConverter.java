package options.papertrading.util.converters;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.models.option.Option;
import options.papertrading.util.mappers.OptionMapper;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class OptionConverter {
    private final IPositionSetter positionSetter;
    private final OptionMapper optionMapper;

    public OptionDto convertToOptionDto(@NonNull Option option) {
        OptionDto optionDto = optionMapper.convertToOptionDto(option);
        optionDto.setPrice(positionSetter.countPriceInRur(option));
        return optionDto;
    }

    public List<OptionDto> convertListToOptionDtoList(@NonNull List<Option> options) {
        return options.parallelStream()
                .map(this::convertToOptionDto)
                .collect(Collectors.toList());
    }

    public OptionDto createOptionDtoFromView(String id, int volume, int buyOrWrite) {
        OptionDto optionDto = new OptionDto();
        optionDto.setId(id);
        optionDto.setVolume(volume);
        optionDto.setBuyOrWrite(buyOrWrite);
        log.info("Get OptionDto from view: {}", optionDto);
        return optionDto;
    }
}
