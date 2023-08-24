package options.papertrading.util.validators;

import options.papertrading.models.portfolio.Portfolio;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class VolumeValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Portfolio.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Portfolio portfolio = (Portfolio) target;
        if (portfolio.getVolume() <= 0) {
            errors.rejectValue("volume", "", "Volume should not be empty");
        }
    }
}
