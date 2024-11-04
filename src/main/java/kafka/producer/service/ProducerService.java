package kafka.producer.service;

import kafka.producer.model.dto.TradeCreatedEventDto;
import java.util.concurrent.ExecutionException;

public interface ProducerService {
    String createEvent(TradeCreatedEventDto tradeCreatedEventDto) throws ExecutionException, InterruptedException;
}
