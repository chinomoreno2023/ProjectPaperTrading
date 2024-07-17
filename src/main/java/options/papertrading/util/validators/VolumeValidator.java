package options.papertrading.util.validators;

import options.papertrading.dto.option.OptionDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class VolumeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OptionDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OptionDto optionDto = (OptionDto) target;
        if (optionDto.getVolume() <= 0) {
            errors.rejectValue("volume", "", "Объём не может быть равен нулю");
        }
    }
}