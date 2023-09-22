package options.papertrading.util.validators;

import options.papertrading.util.TestYourIq;
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
        if (testYourIq.getResult() != testYourIq.getNumber2() * 100 / testYourIq.getNumber1())
            errors.rejectValue("result", "", "Result is wrong");
    }
}