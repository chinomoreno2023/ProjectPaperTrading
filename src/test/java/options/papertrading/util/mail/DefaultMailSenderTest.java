package options.papertrading.util.mail;

import jakarta.mail.MessagingException;
import options.papertrading.models.person.Person;
import options.papertrading.util.converters.TextConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultMailSenderTest {

    @Mock
    private SmtpMailSender smtpMailSender;

    @Mock
    private TextConverter textConverter;

    @InjectMocks
    private DefaultMailSender defaultMailSender;

    String email = "to@example.com";
    String subject = "Восстановление пароля";
    String message = "Code for %s: %s";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                defaultMailSender, "pathToPasswordResetText", "path");
    }

    @Test
    void sendMailForResetPassword() throws IOException, MessagingException {
        Person person = new Person();
        person.setUsername("user");
        person.setActivationCode("123");
        person.setEmail(email);

        when(textConverter.readMailTextFromFile(anyString())).thenReturn("Code for %s: %s");
        doNothing().when(smtpMailSender).sendHtml(eq(email), eq(subject), anyString());

        defaultMailSender.sendMailForResetPassword(person);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        assertAll(
                () -> verify(smtpMailSender).sendHtml(eq(email), eq(subject), messageCaptor.capture()),
                () -> assertEquals("Code for user: 123", messageCaptor.getValue())
        );
    }

    @Test
    void sendMailForResetPassword_ShouldThrowRuntimeException_WhenFileReadFails() throws IOException {
        Person person = new Person();
        person.setEmail(email);
        when(textConverter.readMailTextFromFile(anyString())).thenThrow(new IOException("File error"));

        assertThrows(RuntimeException.class,
                () -> defaultMailSender.sendMailForResetPassword(person));
    }

    @Test
    void sendMailForResetPassword_ShouldThrow_WhenPersonIsNull() {
        Person person = null;
        assertThrows(NullPointerException.class,
                () -> defaultMailSender.sendMailForResetPassword(person));
    }

    @Test
    void sendMail() throws MessagingException {
        doNothing().when(smtpMailSender).sendHtml(email, subject, message);

        defaultMailSender.sendMail(email, subject, message);

        verify(smtpMailSender).sendHtml(email, subject, message);
    }

    @Test
    void sendMail_ThrowsMessagingException_OnError() throws MessagingException {
        doThrow(new MessagingException()).when(smtpMailSender).sendHtml(email, subject, message);

        assertThrows(MessagingException.class, () -> defaultMailSender.sendMail(email, subject, message));
    }
}