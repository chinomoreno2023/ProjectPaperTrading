package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJsonOptionFacade;
import options.papertrading.models.option.Option;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.util.mappers.OptionMapper;
import options.papertrading.util.validators.VolumeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OptionsService implements IJsonOptionFacade {
    private final OptionsRepository optionsRepository;
    private final OptionMapper optionMapper;
    private final VolumeValidator volumeValidator;

    @Transactional(readOnly = true)
    public List<Option> findAll() {
        return optionsRepository.findAll();
    }

    public Option findByOptionId(@NonNull String id) {
        Option option = optionsRepository.findOneById(id);
        log.info("Finding option by id: {}. Result: {}", id, option);
        return option;
    }

    public OptionDto convertToOptionDto(@NonNull Option option) {
        log.info("Converting {} to optionDto", option);
        return optionMapper.convertToOptionDto(option);
    }

    @Transactional(readOnly = true)
    public Option findByStrikeAndTypeAndDaysToMaturity(int strike, @NonNull String type, int daysToMaturity) {
        Option option = optionsRepository.findByStrikeAndTypeAndDaysToMaturity(strike, type, daysToMaturity);
        log.info("Finding option by strike '{}', type '{}' and {} days to maturity", strike, type, daysToMaturity);
        return option;
    }

    public void volumeValidate(@NonNull OptionDto optionDto, @NonNull BindingResult bindingResult) {
        log.info("Validating {}", optionDto);
        volumeValidator.validate(optionDto, bindingResult);
    }

    @Transactional(readOnly = true)
    public List<OptionDto> showOptionsList() {
        return convertListToOptionDtoList(findAll());
    }

    public List<OptionDto> convertListToOptionDtoList(@NonNull List<Option> options) {
        log.info("Converting {} list to optionDto list", options);
        return options.stream()
                      .map(this::convertToOptionDto)
                      .collect(Collectors.toList());
    }
}