package options.papertrading.util.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpMailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private SmtpMailSender smtpMailSender;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(smtpMailSender, "username", "noreply@example.com");
    }

    @Test
    void sendHtml() throws MessagingException {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        smtpMailSender.sendHtml("to@example.com", "Subject", "<html>Test</html>");

        assertAll(
                () -> verify(javaMailSender).createMimeMessage(),
                () -> verify(javaMailSender).send(mimeMessage)
        );
    }

    @Test
    void sendHtml_ShouldThrowMailException_OnError() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailAuthenticationException("SMTP error")).when(javaMailSender).send(mimeMessage);

        assertThrows(MailException.class,
                () -> smtpMailSender.sendHtml("to@example.com", "Subject", "Content"));
    }

    @Test
    void sendHtml_ShouldThrow_WhenInputIsNull() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> smtpMailSender.sendHtml(null, "subject", "message")),
                () -> assertThrows(NullPointerException.class,
                        () -> smtpMailSender.sendHtml("emailTo", null, "message")),
                () -> assertThrows(NullPointerException.class,
                        () -> smtpMailSender.sendHtml("emailTo", "subject", null))
        );
    }
}