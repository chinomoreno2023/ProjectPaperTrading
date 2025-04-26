package kafka.consumer.handler;

import kafka.consumer.exception.NonRetryableException;
import kafka.consumer.exception.RetryableException;
import kafka.consumer.persistence.entity.ProcessedEventEntity;
import kafka.consumer.persistence.repository.ProcessedEventRepository;
import kafka.producer.model.TradeCreatedEvent;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeCreatedEventHandlerImplTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private PortfoliosRepository portfoliosRepository;

    @InjectMocks
    private TradeCreatedEventHandlerImpl handler;

    @Test
    void handle_ShouldDeletePortfolio() {
        TradeCreatedEvent event = new TradeCreatedEvent();
        event.setPortfolioForDelete(new Portfolio());

        handler.handle(event, "messageId", "key");

        verify(portfoliosRepository).delete(event.getPortfolioForDelete());
        verify(processedEventRepository).save(any(ProcessedEventEntity.class));
    }

    @Test
    void handle_ShouldSavePortfolio() {
        TradeCreatedEvent event = new TradeCreatedEvent();
        event.setPortfolioForSave(new Portfolio());

        handler.handle(event, "messageId", "key");

        verify(portfoliosRepository).save(event.getPortfolioForSave());
        verify(processedEventRepository).save(any(ProcessedEventEntity.class));
    }

    @Test
    void handle_ShouldThrowRetryableException_OnResourceAccessException() {
        TradeCreatedEvent event = new TradeCreatedEvent();
        event.setPortfolioForSave(new Portfolio());

        when(portfoliosRepository.save(any())).thenThrow(ResourceAccessException.class);

        assertThrows(RetryableException.class,
                () -> handler.handle(event, "messageId", "key"));
    }

    @Test
    void handle_ShouldThrowNonRetryableException_OnHttpServerErrorException() {
        TradeCreatedEvent event = new TradeCreatedEvent();
        event.setPortfolioForSave(new Portfolio());

        when(portfoliosRepository.save(any())).thenThrow(HttpServerErrorException.class);

        assertThrows(NonRetryableException.class,
                () -> handler.handle(event, "messageId", "key"));
    }

    @Test
    void handle_ShouldThrowNonRetryableException_OnGenericException() {
        TradeCreatedEvent event = new TradeCreatedEvent();
        event.setPortfolioForSave(new Portfolio());

        when(portfoliosRepository.save(any())).thenThrow(RuntimeException.class);

        assertThrows(NonRetryableException.class,
                () -> handler.handle(event, "messageId", "key"));
    }

    @Test
    void handle_ShouldThrowNonRetryableException_OnDuplicateMessage() {
        TradeCreatedEvent event = new TradeCreatedEvent();

        when(processedEventRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(NonRetryableException.class,
                () -> handler.handle(event, "messageId", "key"));
    }

    @Test
    void kafkaListenerAnnotation_ShouldHaveCorrectConfiguration() {
        KafkaListener annotation = TradeCreatedEventHandlerImpl.class
                .getAnnotation(KafkaListener.class);

        assertThat(annotation.topics()).containsExactly("trade-created-events-topic");
    }

    @Test
    void transactionalAnnotation_ShouldHaveCorrectManager() {
        Method handleMethod = getHandleMethod();
        Transactional transactional = handleMethod.getAnnotation(Transactional.class);

        assertThat(transactional.transactionManager()).isEqualTo("transactionManager");
    }

    @Test
    void payloadAnnotation_ShouldBeOnFirstParameter() {
        Parameter parameter = getHandleMethod().getParameters()[0];
        assertThat(parameter.isAnnotationPresent(Payload.class)).isTrue();
    }

    @Test
    void headerAnnotations_ShouldHaveCorrectValues() {
        Parameter[] parameters = getHandleMethod().getParameters();

        assertAll(
                () -> {
                    Header header = parameters[1].getAnnotation(Header.class);
                    assertThat(header.value()).isEqualTo("messageId");
                    assertThat(header.required()).isTrue();
                },
                () -> {
                    Header header = parameters[2].getAnnotation(Header.class);
                    assertThat(header.value()).isEqualTo(KafkaHeaders.RECEIVED_KEY);
                    assertThat(header.required()).isTrue();
                }
        );
    }

    @Test
    void kafkaHandlerAnnotation_ShouldBePresent() {
        assertThat(getHandleMethod().isAnnotationPresent(KafkaHandler.class)).isTrue();
    }

    private Method getHandleMethod() {
        try {
            return TradeCreatedEventHandlerImpl.class.getDeclaredMethod(
                    "handle",
                    TradeCreatedEvent.class,
                    String.class,
                    String.class
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Method not found", e);
        }
    }

    @Test
    void handle_ShouldNotProcess_WhenMessageAlreadyProcessed() {
        TradeCreatedEvent event = new TradeCreatedEvent();

        when(processedEventRepository.findByMessageId("messageId"))
                .thenReturn(new ProcessedEventEntity());

        handler.handle(event, "messageId", "key");

        assertAll(
                () -> verify(portfoliosRepository, never()).delete(any()),
                () -> verify(portfoliosRepository, never()).save(any()),
                () -> verify(processedEventRepository, never()).save(any())
        );
    }
}