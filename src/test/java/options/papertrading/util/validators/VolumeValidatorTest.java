package options.papertrading.util.validators;

import options.papertrading.dto.option.OptionDto;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import static org.junit.jupiter.api.Assertions.*;

class VolumeValidatorTest {
    private VolumeValidator volumeValidator = new VolumeValidator();

    @Test
    void supports() {
        assertTrue(volumeValidator.supports(OptionDto.class));
    }

    @Test
    void supports_ShouldReturnFalse() {
        assertFalse(volumeValidator.supports(String.class));
    }

    @Test
    void supports_ShouldReturnFalse_OnNull() {
        assertFalse(volumeValidator.supports(null));
    }

    @Test
    void validate() {
        OptionDto optionDto = new OptionDto();
        optionDto.setVolume(0);
        Errors errors = new BeanPropertyBindingResult(optionDto, "optionDto");

        volumeValidator.validate(optionDto, errors);

        assertAll (
                () -> assertTrue(errors.hasErrors()),
                () -> assertNotNull(errors.getFieldError("volume")),
                () -> assertEquals("Объём не может быть равен нулю",
                        errors.getFieldError("volume").getDefaultMessage()),
                () -> assertEquals(1, errors.getErrorCount())
        );
    }

    @Test
    void validate_shouldNotAddErrors_whenDataIsValid() {
        OptionDto optionDto = new OptionDto();
        optionDto.setVolume(1);
        Errors errors = new BeanPropertyBindingResult(optionDto, "optionDto");

        volumeValidator.validate(optionDto, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_shouldThrowException_whenVolumeLessThanZero() {
        OptionDto optionDto = new OptionDto();
        optionDto.setVolume(-1);
        Errors errors = new BeanPropertyBindingResult(optionDto, "optionDto");

        volumeValidator.validate(optionDto, errors);

        assertAll (
                () -> assertTrue(errors.hasErrors()),
                () -> assertNotNull(errors.getFieldError("volume")),
                () -> assertEquals("Объём не может быть равен нулю",
                        errors.getFieldError("volume").getDefaultMessage()),
                () -> assertEquals(1, errors.getErrorCount())
        );
    }
}