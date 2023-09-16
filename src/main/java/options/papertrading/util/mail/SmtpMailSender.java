package options.papertrading.util.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class SmtpMailSender {
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    public SmtpMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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