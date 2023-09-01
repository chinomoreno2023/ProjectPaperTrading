package options.papertrading.services;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.util.mappers.OptionMapper;
import options.papertrading.util.validators.VolumeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OptionsService {
    private final OptionsRepository optionsRepository;
    private final OptionMapper optionMapper;
    private final VolumeValidator volumeValidator;

    @Transactional(readOnly = true)
    public List<Option> findAll() {
        return optionsRepository.findAll();
    }

    public OptionDto convertToOptionDto(Option option) {
        return optionMapper.convertToOptionDto(option);
    }

    @Transactional(readOnly = true)
    public Option findByStrikeAndTypeAndDaysToMaturity(int strike, String type, int daysToMaturity) {
        return optionsRepository.findByStrikeAndTypeAndDaysToMaturity(strike, type, daysToMaturity);
    }

    public void validate(OptionDto optionDto, BindingResult bindingResult) {
        volumeValidator.validate(optionDto, bindingResult);
    }

    @Transactional(readOnly = true)
    public List<OptionDto> showOptionsList() {
        return convertListToOptionDtoList(findAll());
    }

    public List<OptionDto> convertListToOptionDtoList(List<Option> options) {
        return options.stream()
                      .map(this::convertToOptionDto)
                      .collect(Collectors.toList());
    }
}