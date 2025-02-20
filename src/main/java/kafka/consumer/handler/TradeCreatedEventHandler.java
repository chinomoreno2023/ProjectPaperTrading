package kafka.consumer.handler;

import kafka.producer.model.TradeCreatedEvent;

public interface TradeCreatedEventHandler {
    void handle(TradeCreatedEvent tradeCreatedEvent, String messageId, String messageKey);
}
