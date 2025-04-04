package options.papertrading.util.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorServiceTest {

    @Test
    void isPasswordStrong_testNullRule() {
        String password = null;
        assertThrows(NullPointerException.class,
                () -> new PasswordValidatorService().isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong_testLengthRule() {
        String password = "123456";
        assertFalse(new PasswordValidatorService().isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong_testCharacterRule() {
        String password = "asdqweweecf";
        assertFalse(new PasswordValidatorService().isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong_testWhitespaceRule() {
        String password = "123sdf 456fds";
        assertFalse(new PasswordValidatorService().isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong_testRepeatCharacterRegexRule() {
        String password = "1114eefefe56";
        assertFalse(new PasswordValidatorService().isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong() {
        String password = "sdfgericjh6";
        assertTrue(new PasswordValidatorService().isPasswordStrong(password));
    }

    @Test
    void isPasswordStrong_ShouldHandle_LongPasswords() {
        String longPassword = "A1" + "bcd".repeat(32) + "!";
        assertTrue(new PasswordValidatorService().isPasswordStrong(longPassword));
    }

    @Test
    void isPasswordStrong_TestLongPasswords() {
        String longPassword = "A1" + "bcdfgert".repeat(33) + "!";
        assertFalse(new PasswordValidatorService().isPasswordStrong(longPassword));
    }

    @Test
    void isPasswordStrong_ShouldFail_OnEmptyString() {
        assertFalse(new PasswordValidatorService().isPasswordStrong(""));
    }
}