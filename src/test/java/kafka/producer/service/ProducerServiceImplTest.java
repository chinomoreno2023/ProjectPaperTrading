package kafka.producer.service;

import kafka.producer.model.TradeCreatedEvent;
import kafka.producer.model.dto.TradeCreatedEventDto;
import options.papertrading.models.portfolio.Portfolio;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerServiceImplTest {

    @Mock
    private KafkaTemplate<String, TradeCreatedEvent> kafkaTemplate;

    @InjectMocks
    private ProducerServiceImpl producerService;

    private TradeCreatedEventDto tradeCreatedEventDto;

    @BeforeEach
    void setUp() {
        tradeCreatedEventDto = new TradeCreatedEventDto(new Portfolio(), new Portfolio());
    }

    @Test
    void createEvent_success() throws Exception {
        TradeCreatedEventDto eventDto = new TradeCreatedEventDto(null, null);

        SendResult<String, TradeCreatedEvent> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);

        when(metadata.topic()).thenReturn("trade-created-events-topic");
        when(metadata.partition()).thenReturn(0);
        when(metadata.offset()).thenReturn(100L);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(sendResult));

        String resultId = producerService.createEvent(eventDto);

        assertAll(
                () -> assertNotNull(resultId),
                () -> verify(kafkaTemplate).send(any(ProducerRecord.class)),
                () -> assertNotNull(AnnotationUtils.findAnnotation(
                        ProducerServiceImpl.class.getMethod("createEvent", TradeCreatedEventDto.class),
                        Transactional.class))
        );
    }

    @Test
    void createEvent_Failure() {
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.failedFuture(new ExecutionException("Kafka error", new RuntimeException())));

        assertAll(
                () -> assertThrows(RuntimeException.class, () -> producerService.createEvent(tradeCreatedEventDto)),
                () -> verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class))
        );
    }
}
