package options.papertrading.services.processors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.services.processors.interfaces.IAdditionStrategyProcessor;
import options.papertrading.services.strategies.AdditionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdditionStrategyProcessor implements IAdditionStrategyProcessor {
    private final Map<AdditionStrategyType, AdditionStrategy> strategies;

    @Autowired
    public AdditionStrategyProcessor(List<AdditionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(AdditionStrategy::getType, Function.identity()));
    }

    @Override
    @Transactional
    public void process(@NonNull AdditionStrategyType strategyType, @NonNull OptionDto optionDto) {
        AdditionStrategy strategy = strategies.get(strategyType);
        log.info("Strategy type: {}", strategyType);
        strategy.addPortfolio(optionDto);
    }
}
