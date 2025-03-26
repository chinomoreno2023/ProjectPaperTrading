package options.papertrading.util.validators;

import options.papertrading.models.TestYourIq;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TestIqValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return TestYourIq.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        TestYourIq testYourIq = (TestYourIq) target;
        if (testYourIq.getResult() != (testYourIq.getNumber2() * 100 / testYourIq.getNumber1()) - 100)
            errors.rejectValue("result", "",
                    "Если вы не можете решить даже такую задачу, то вряд ли вы готовы к торговле опционами");
    }
}