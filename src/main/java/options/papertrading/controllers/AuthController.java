package options.papertrading.controllers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.facade.AuthFacade;
import options.papertrading.facade.PersonFacade;
import options.papertrading.models.person.Person;
import options.papertrading.models.TestYourIq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthFacade authFacade;
    private final PersonFacade personFacade;

    @Value("${spring.mail.username}")
    private String myMail;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person")
                                      @Valid @NonNull Person person,
                                      @NonNull BindingResult bindingResult, Model model) {
        log.info("Adding new person: {}", person);
        authFacade.validate(person, bindingResult);
        if (bindingResult.hasErrors())
            return "auth/registration";

        try {
            authFacade.register(person);
        } catch (RuntimeException exception) {
            model.addAttribute("emailError", "Произошла ошибка: " + exception.getMessage());
            return "auth/registration";
        }

        return "auth/confirmation";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable @NonNull String code) {
        log.info("activation code: {}", code);
        if (authFacade.activatePerson(code))
            return "auth/activation";

        return "auth/wrong_code";
    }

    @GetMapping("/hello")
    public String testYourIq(Model model) {
        model.addAttribute("testYourIq", authFacade.testYourIq());
        return "auth/hello";
    }

    @PostMapping("/hello")
    public String testYourIq(@NonNull @ModelAttribute("testYourIq") TestYourIq testYourIq,
                             @NonNull BindingResult bindingResult,
                             @NonNull @ModelAttribute("person") Person person) {
        log.info("First number - {}, second number - {}, result - {}", testYourIq.getNumber1(),
                                                                       testYourIq.getNumber2(),
                                                                       testYourIq.getResult());
        authFacade.testYourIqValidate(testYourIq, bindingResult);
        if (bindingResult.hasErrors())
            return "auth/hello";

        return "auth/registration";
    }

    @PostMapping(value = "/restore", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> checkMailForRestorePassword(@RequestParam("emailForRestore")
                                                              @NonNull String email) {
        log.info("email for restore: {}", email);
        try {
            Person person = personFacade.updateActivationCode(email);
            personFacade.sendMailForResetPassword(person);
            return ResponseEntity.ok("Следуйте инструкциям в письме");
        } catch (NullPointerException exception){
            return ResponseEntity.badRequest().body("Пользователь с такой почтой не найден");
        } catch (MessagingException | IOException exception) {
            log.info("Exception: {}", exception);
            return ResponseEntity.badRequest().body("Пользователь с такой почтой найден, но не получилось отправить письмо");
        }
    }

    @GetMapping("/restore/{code}")
    public String requestForRestorePassword(@PathVariable @NonNull String code, Model model) {
        Person person = personFacade.findByActivationCode(code);
        if (person == null)
            return "auth/wrong_code";

        model.addAttribute("code", code);
        return "auth/restorePassword";
    }

    @PostMapping(value = "/restore/confirmation", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> restorePassword(@RequestParam("password") String password,
                                                  @RequestParam("retypedPassword") String retypedPassword,
                                                  @RequestParam("code") String code) {
        if (password.isEmpty() || retypedPassword.isEmpty())
            return ResponseEntity.badRequest().body("Поля не могут быть пустыми");

        Person person = personFacade.findByActivationCode(code);
        if (password.equals(retypedPassword)) {
            if (!authFacade.validate(password))
                return ResponseEntity.badRequest().body("Пароль должен быть не менее восьми символов с хотя бы одной цифрой");

            personFacade.updatePassword(person, password);
            return ResponseEntity.ok("Пароль успешно изменен");
        }

        return ResponseEntity.badRequest().body("Пароли не совпадают");
    }

    @GetMapping("/profile")
    public String openUserPage(Model model) {
        Person person = personFacade.getCurrentPerson();
        model.addAttribute("person", person);
        return "auth/profile";
    }

    @PostMapping(value = "/profile", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> updatePassword(@RequestParam("password") String password,
                                                 @RequestParam("retypedPassword") String retypedPassword) {
        if (password.isEmpty() || retypedPassword.isEmpty())
            return ResponseEntity.badRequest().body("Поля не могут быть пустыми");

        if (password.equals(retypedPassword)) {
            personFacade.updatePassword(personFacade.getCurrentPerson(), password);
            return ResponseEntity.ok("Пароль успешно изменен");
        }

        return ResponseEntity.badRequest().body("Пароли не совпадают");
    }

    @PostMapping(value = "/profile/feedback", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> sendFeedback(@RequestParam("subject") String subject,
                                               @RequestParam("message") String message) {
        log.info("subject: {}, message: {}", subject, message);
        if (subject.isEmpty() || message.isEmpty())
            return ResponseEntity.badRequest().body("Поля не могут быть пустыми");

        try {
            String mailboxOfUser = "<br><br>Почта пользователя:<br>" + personFacade.getCurrentPerson().getEmail();
            String htmlMessage = message.replace("\n", "<br>") + mailboxOfUser;
            personFacade.sendMail(myMail, subject, htmlMessage);
            return ResponseEntity.ok("Отправлено");
        } catch (MessagingException exception) {
            log.info("Exception: {}", exception);
            return ResponseEntity.internalServerError().body("Не получилось отправить письмо");
        }
    }
}