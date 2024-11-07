package kafka.consumer.handler;

import kafka.consumer.exception.NonRetryableException;
import kafka.consumer.exception.RetryableException;
import kafka.consumer.persistence.entity.ProcessedEventEntity;
import kafka.consumer.persistence.repository.ProcessedEventRepository;
import kafka.producer.model.TradeCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.repositories.PortfoliosRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@Component
@KafkaListener(topics = "trade-created-events-topic")
@RequiredArgsConstructor
public class TradeCreatedEventHandler {
    private final ProcessedEventRepository processedEventRepository;
    private final PortfoliosRepository portfoliosRepository;

    @KafkaHandler
    @Transactional(transactionManager = "transactionManager")
    public void handle(@Payload TradeCreatedEvent tradeCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {

        log.info("Received event: {}", tradeCreatedEvent.getEventId());
        ProcessedEventEntity processedEventEntity = processedEventRepository.findByMessageId(messageId);
        if (processedEventEntity != null) {
            log.info("Duplicate message id: {}", messageId);
            return;
        }

        try {
            if (tradeCreatedEvent.getPortfolioForDelete() != null) {
                portfoliosRepository.delete(tradeCreatedEvent.getPortfolioForDelete());
            }
            if (tradeCreatedEvent.getPortfolioForSave() != null) {
                portfoliosRepository.save(tradeCreatedEvent.getPortfolioForSave());
            }
        }
        catch (ResourceAccessException exception) {
            log.error(exception.getMessage());
            throw new RetryableException(exception);
        }
        catch (HttpServerErrorException exception) {
            log.error(exception.getMessage());
            throw new NonRetryableException(exception);
        }
        catch (Exception exception) {
            log.error(exception.getMessage());
            throw new NonRetryableException(exception);
        }

        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, tradeCreatedEvent.getEventId()));
        }
        catch (DataIntegrityViolationException exception) {
            log.error(exception.getMessage());
            throw new NonRetryableException(exception);
        }
    }
}
