package options.papertrading.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.facade.interfaces.IPortfolioFacade;
import options.papertrading.facade.interfaces.IPortfolioFacadeHtmlVersion;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.OptionsRepository;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IAdditionStrategyProcessor;
import options.papertrading.services.processors.interfaces.IMarginCallService;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.converters.OptionConverter;
import options.papertrading.util.converters.PortfolioConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfoliosService implements IPortfolioFacadeHtmlVersion {
    private final PortfoliosRepository portfoliosRepository;
    private final IPersonFacadeHtmlVersion personsService;
    private final OptionsRepository optionsRepository;
    private final IPositionSetter positionSetter;
    private final BeanFactory beanFactory;
    private final OptionConverter optionConverter;
    private final PortfolioConverter portfolioConverter;
    private final IMarginCallService marginCallService;
    private final IPortfolioManager portfolioManager;
    private final IAdditionStrategyProcessor strategy;
    private static final int BUY = 1;
    private static final int WRITE = -1;

    @Transactional(readOnly = true)
    public Portfolio findByOwnerAndOption(@NonNull Person owner, @NonNull Option option) {
        log.info("Request to find by {} and {}", owner, option);
        return portfoliosRepository.findByOwnerAndOption(owner, option);
    }

    public List<PortfolioDto> showAllPortfolios() {
        portfolioManager.updateCurrentNetPositionAndOpenLimit();
        portfolioManager.updateAllVariatMargin();
        marginCallService.marginCallCheck();
        return portfolioConverter.convertListToPortfolioDtoList(beanFactory.getBean(PortfoliosService.class)
                .findAllByOwner(personsService.getCurrentPerson()));
    }

    @Transactional(readOnly = true)
    public List<Portfolio> findAllByOwner(@NonNull Person owner) {
        return portfoliosRepository.findAllByOwner(owner);
    }

    @Transactional
    public void addPortfolio(@NonNull String id, int volume, int buyOrWrite) {
        beanFactory.getBean(IPortfolioFacade.class)
                .addPortfolio(optionConverter.createOptionDtoFromView(id, volume, buyOrWrite));
    }

    @Transactional
    public void addPortfolio(@NonNull OptionDto optionDto) {
        Portfolio portfolio = portfolioManager.checkOrderTime(optionDto);
        log.info("Portfolio: {}", portfolio);

        if (positionSetter.isContained(portfolio)) {
            beanFactory.getBean(PortfoliosService.class).addPortfolioIfItIsContained(optionDto);
        } else {
            beanFactory.getBean(PortfoliosService.class).addPortfolioIfItIsNotContained(optionDto);
        }
    }

    @Transactional
    public void addPortfolioIfItIsNotContained(@NonNull OptionDto optionDto) {
        switch (optionDto.getBuyOrWrite()) {
            case BUY:
                strategy.process(AdditionStrategyType.BUYING_IF_NOT_CONTAINED, optionDto);
                break;
            case WRITE:
                strategy.process(AdditionStrategyType.WRITING_IF_NOT_CONTAINED, optionDto);
                break;
            default:
                log.error("Unknown buyOrWrite: {}", optionDto.getBuyOrWrite());
                throw new IllegalArgumentException();
        }
    }

    @Transactional
    public void addPortfolioIfItIsContained(@NonNull OptionDto optionDto) {
        Portfolio newPortfolio = portfolioConverter.convertOptionDtoToPortfolio(optionDto);
        Portfolio oldPortfolio = beanFactory.getBean(PortfoliosService.class)
                .findByOwnerAndOption(personsService.getCurrentPerson(),
                        optionsRepository.findOneById(newPortfolio.getOption().getId()));
        log.info("Old portfolio is {}", newPortfolio);
        log.info("New portfolio is {}", oldPortfolio);

        switch (optionDto.getBuyOrWrite()) {
            case BUY:
                if (positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)) {
                    strategy.process(AdditionStrategyType.DIRECT_BUYING_IF_CONTAINED, optionDto);
                } else strategy.process(AdditionStrategyType.REVERSE_BUYING_IF_CONTAINED, optionDto);
                break;

            case WRITE:
                if (positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)) {
                    strategy.process(AdditionStrategyType.DIRECT_WRITING_IF_CONTAINED, optionDto);
                } else strategy.process(AdditionStrategyType.REVERSE_WRITING_IF_CONTAINED, optionDto);
                break;

                default:
                log.error("Unknown buyOrWrite: {}", optionDto.getBuyOrWrite());
                throw new IllegalArgumentException();
        }
    }
}