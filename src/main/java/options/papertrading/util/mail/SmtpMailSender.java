package options.papertrading.util.mail;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Slf4j
@Component
public class SmtpMailSender {
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${email.image.path}")
    private String imagePath;

    @Autowired
    public SmtpMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtml(@NonNull String emailTo,
                         @NonNull String subject,
                         @NonNull String message) throws MessagingException {
        log.info("Sending '{}' message with '{}' subject to '{}' email", message, subject, emailTo);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(message, true);
        helper.addInline("logo", new File(imagePath));
        mailSender.send(mimeMessage);
    }
}