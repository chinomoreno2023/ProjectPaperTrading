package options.papertrading.util.validators;

import options.papertrading.models.TestYourIq;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import static org.junit.jupiter.api.Assertions.*;

class TestIqValidatorTest {
    private TestIqValidator testIqValidator = new TestIqValidator();

    @Test
    void supports() {
        assertTrue(new TestIqValidator().supports(TestYourIq.class));
    }

    @Test
    void supports_ShouldReturnFalse() {
        assertFalse(new TestIqValidator().supports(String.class));
    }

    @Test
    void supports_ShouldReturnFalse_OnNull() {
        assertFalse(new TestIqValidator().supports(null));
    }

    @Test
    void validate() {
        TestYourIq testYourIq = new TestYourIq(1, 2, 3);
        Errors errors = new BeanPropertyBindingResult(testYourIq, "testYourIq");

        testIqValidator.validate(testYourIq, errors);

        assertAll (
                () -> assertTrue(errors.hasErrors()),
                () -> assertNotNull(errors.getFieldError("result")),
                () -> assertEquals("Если вы не можете решить даже такую задачу, то вряд ли вы готовы к торговле опционами",
                        errors.getFieldError("result").getDefaultMessage()),
                () -> assertEquals(1, errors.getErrorCount())
        );
    }

    @Test
    void validate_shouldNotAddErrors_whenDataIsValid() {
        TestYourIq testYourIq = new TestYourIq(63, 74, 17);
        Errors errors = new BeanPropertyBindingResult(testYourIq, "testYourIq");

        testIqValidator.validate(testYourIq, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_shouldThrowException_whenNumber1IsZero() {
        TestYourIq testYourIq = new TestYourIq(0, 50, 100);
        Errors errors = new BeanPropertyBindingResult(testYourIq, "testYourIq");

        assertThrows(ArithmeticException.class, () -> testIqValidator.validate(testYourIq, errors));
    }

    @Test
    void validate_shouldAddError_whenNegativeNumbersUsed() {
        TestYourIq testYourIq = new TestYourIq(-10, 50, 400);
        Errors errors = new BeanPropertyBindingResult(testYourIq, "testYourIq");

        testIqValidator.validate(testYourIq, errors);

        assertTrue(errors.hasErrors());
    }
}