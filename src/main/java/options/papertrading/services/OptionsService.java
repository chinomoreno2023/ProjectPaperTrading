package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IOptionFacade;
import options.papertrading.facade.interfaces.IPositionSetter;
import options.papertrading.models.option.Option;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.util.mappers.OptionMapper;
import options.papertrading.util.validators.VolumeValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OptionsService implements IOptionFacade {
    private final OptionsRepository optionsRepository;
    private final OptionMapper optionMapper;
    private final VolumeValidator volumeValidator;
    private final IPositionSetter positionSetter;

    @Transactional(readOnly = true)
    public List<Option> findAll() {
        return optionsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OptionDto> showOptionsListByPrefix(String prefix) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalTime timeToCheck = LocalTime.of(14, 00);

        if (!currentTime.toLocalTime().isBefore(timeToCheck)) {
            List<OptionDto> options = optionsRepository.findByIdStartingWith(prefix)
                                                       .stream()
                                                       .filter(option -> option.getDaysToMaturity() > 0)
                                                       .sorted(Comparator.comparing(Option::getStrike))
                                                       .map(this::convertToOptionDto)
                                                       .collect(Collectors.toList());
            return options;
        }

        List<OptionDto> options = optionsRepository.findByIdStartingWith(prefix)
                                                   .stream()
                                                   .filter(option -> option.getDaysToMaturity() >= 0)
                                                   .sorted(Comparator.comparing(Option::getStrike))
                                                   .map(this::convertToOptionDto)
                                                   .collect(Collectors.toList());
        return options;
    }

    @Transactional(readOnly = true)
    public List<OptionDto> showOptionsFromCurrentPortfolios(List<PortfolioDto> portfolios) {
        List<OptionDto> options = portfolios.stream()
                                            .map(portfolio -> convertToOptionDto(findByOptionId(portfolio.getId())))
                                            .collect(Collectors.toList());
        log.info("Finding options from current portfolios. Result: {}", options);
        return options;
    }

    @Transactional(readOnly = true)
    public Option findByOptionId(@NonNull String id) {
        Option option = optionsRepository.findOneById(id);
        log.info("Finding option by id: {}. Result: {}", id, option);
        return option;
    }

    public OptionDto convertToOptionDto(@NonNull Option option) {
        OptionDto optionDto = optionMapper.convertToOptionDto(option);
        optionDto.setPrice(positionSetter.countPriceInRur(option));
        return optionDto;
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
        return options.stream()
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