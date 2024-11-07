package options.papertrading.util.validators;

import org.passay.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class PasswordValidatorService {

    public boolean isPasswordStrong(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 100), // минимальная длина 8, максимальная 30
//                new CharacterRule(EnglishCharacterData.UpperCase, 1), // минимум 1 заглавная буква
                new CharacterRule(EnglishCharacterData.Digit, 1), // минимум 1 цифра
//                new CharacterRule(EnglishCharacterData.Special, 1), // минимум 1 специальный символ
                new WhitespaceRule(), // не должно быть пробелов
//                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false), // запрещает последовательности букв длиной 3 и более
//                new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false), // запрещает последовательности цифр длиной 3 и более
//                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 3, false), // запрещает последовательности клавиш на клавиатуре длиной 3 и более
                new RepeatCharacterRegexRule(3) // запрещает повторение одного и того же символа более 3 раз подряд
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        return result.isValid();
    }
}