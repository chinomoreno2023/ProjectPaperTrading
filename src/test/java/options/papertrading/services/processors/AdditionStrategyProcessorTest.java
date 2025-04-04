package options.papertrading.services.processors;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.AdditionStrategyType;
import options.papertrading.services.strategies.AdditionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdditionStrategyProcessorTest {

    private AdditionStrategyProcessor processor;

    @Mock
    private AdditionStrategy mockStrategy;

    @Mock
    private OptionDto optionDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockStrategy.getType()).thenReturn(AdditionStrategyType.BUYING_IF_NOT_CONTAINED);
        processor = new AdditionStrategyProcessor(List.of(mockStrategy));
    }

    @Test
    void process_ShouldInvokeCorrectStrategy_WhenStrategyExists() {
        processor.process(AdditionStrategyType.BUYING_IF_NOT_CONTAINED, optionDto);

        verify(mockStrategy, times(1)).addPortfolio(optionDto);
    }

    @Test
    void process_ShouldThrowException_WhenStrategyDoesNotExist() {
        assertThrows(NullPointerException.class, () ->
                processor.process(AdditionStrategyType.WRITING_IF_NOT_CONTAINED, optionDto)
        );
    }

    @Test
    void process_ShouldThrowNullPointerException_WhenStrategyTypeIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                processor.process(null, optionDto)
        );

        assertEquals("strategyType is marked non-null but is null", exception.getMessage());
    }

    @Test
    void process_ShouldThrowNullPointerException_WhenOptionDtoIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                processor.process(AdditionStrategyType.BUYING_IF_NOT_CONTAINED, null)
        );

        assertEquals("optionDto is marked non-null but is null", exception.getMessage());
    }

    @Test
    void process_ShouldBeTransactional() throws NoSuchMethodException {
        Method method = AdditionStrategyProcessor.class.getMethod(
                "process", AdditionStrategyType.class, OptionDto.class);
        assertTrue(method.isAnnotationPresent(Transactional.class));
    }
}
