package kafka.producer.service;

import kafka.producer.model.TradeCreatedEvent;
import kafka.producer.model.dto.TradeCreatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProducerServiceImpl implements ProducerService {
    private final KafkaTemplate<String, TradeCreatedEvent> kafkaTemplate;

    @Override
    @Transactional
    public String createEvent(TradeCreatedEventDto tradeCreatedEventDto) throws ExecutionException, InterruptedException {
        String eventId = UUID.randomUUID().toString();

        TradeCreatedEvent tradeCreatedEvent = new TradeCreatedEvent(eventId,
                tradeCreatedEventDto.getId(),
                tradeCreatedEventDto.getVolume(),
                tradeCreatedEventDto.getBuyOrWrite());

        ProducerRecord<String, TradeCreatedEvent> record = new ProducerRecord<>("trade-created-events-topic",
                eventId,
                tradeCreatedEvent);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, TradeCreatedEvent> result = kafkaTemplate.send(record).get();

        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Partition: {}", result.getRecordMetadata().partition());
        log.info("Offset: {}", result.getRecordMetadata().offset());
        log.info("Return: {}", eventId);
        return eventId;
    }
}