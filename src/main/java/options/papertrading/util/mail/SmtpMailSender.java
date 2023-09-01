package options.papertrading.util.mail;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class SmtpMailSender {
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void sendHtml(String emailTo, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(username);
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(message, true);
        mailSender.send(mimeMessage);
    }
}