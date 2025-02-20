package kafka.producer.service;

import kafka.producer.model.dto.TradeCreatedEventDto;

public interface ProducerService {
    String createEvent(TradeCreatedEventDto tradeCreatedEventDto);
}
