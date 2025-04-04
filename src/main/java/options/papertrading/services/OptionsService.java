package options.papertrading.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IOptionFacadeHtmlVersion;
import options.papertrading.models.option.Option;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.util.converters.OptionConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionsService implements IOptionFacadeHtmlVersion {
    private final OptionsRepository optionsRepository;
    private final BeanFactory beanFactory;
    private final OptionConverter optionConverter;

    @Transactional(readOnly = true)
    public List<Option> findAll() {
        return optionsRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "OptionsService::showOptionsListByPrefix", key = "#prefix")
    public List<OptionDto> showOptionsListByPrefix(String prefix) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalTime timeToCheck = LocalTime.of(14, 00);

        if (!currentTime.toLocalTime().isBefore(timeToCheck)) {
            return optionsRepository.findByIdStartingWith(prefix)
                                                       .parallelStream()
                                                       .filter(option -> option.getDaysToMaturity() > 0)
                                                       .sorted(Comparator.comparing(Option::getStrike))
                                                       .map(optionConverter::convertToOptionDto)
                                                       .collect(Collectors.toList());
        }

        return optionsRepository.findByIdStartingWith(prefix)
                                                   .parallelStream()
                                                   .filter(option -> option.getDaysToMaturity() >= 0)
                                                   .sorted(Comparator.comparing(Option::getStrike))
                                                   .map(optionConverter::convertToOptionDto)
                                                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OptionDto> showOptionsFromCurrentPortfolios(List<PortfolioDto> portfolios) {
        List<OptionDto> options = portfolios.parallelStream()
                .map(portfolio -> optionConverter.convertToOptionDto(
                                beanFactory.getBean(OptionsService.class)
                                        .findByOptionId(portfolio.getId())))
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

    @Transactional(readOnly = true)
    public List<OptionDto> showOptionsList() {
        return optionConverter.convertListToOptionDtoList(beanFactory.getBean(OptionsService.class).findAll());
    }
}