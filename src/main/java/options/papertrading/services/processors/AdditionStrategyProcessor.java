package options.papertrading.services.processors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.services.processors.interfaces.IAdditionStrategyProcessor;
import options.papertrading.services.strategies.AdditionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AdditionStrategyProcessor implements IAdditionStrategyProcessor {
    private final Map<String, AdditionStrategy> strategies;

    @Autowired
    public AdditionStrategyProcessor(Map<String, AdditionStrategy> strategies) {
        this.strategies = new HashMap<>(strategies);
    }

    @Override
    @Transactional
    public void process(@NonNull String strategy, @NonNull OptionDto optionDto) {
        Optional.ofNullable(strategies.get(strategy))
                .orElseThrow(() -> new IllegalArgumentException("Unknown strategy: " + strategy))
                .addPortfolio(optionDto);
    }
}
