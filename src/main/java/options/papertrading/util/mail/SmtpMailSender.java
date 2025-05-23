package options.papertrading.util.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmtpMailSender {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    public SmtpMailSender(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }

    public void sendHtml(@NonNull String emailTo,
                         @NonNull String subject,
                         @NonNull String message) throws MessagingException {
        log.info("Sending '{}' message with '{}' subject to '{}' email", message, subject, emailTo);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(message, true);
        javaMailSender.send(mimeMessage);
    }
}