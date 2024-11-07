package options.papertrading.util.validators;

import org.passay.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class PasswordValidatorService {

    public boolean isPasswordStrong(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 100),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new WhitespaceRule(),
                new RepeatCharacterRegexRule(3)
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        return result.isValid();
    }
}