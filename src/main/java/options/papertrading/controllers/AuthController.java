package options.papertrading.controllers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.facade.AuthFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.TestYourIq;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthFacade authFacade;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid @NonNull Person person,
                                      @NonNull BindingResult bindingResult) {
        log.info("Adding new person: {}", person);
        authFacade.validate(person, bindingResult);
        if (bindingResult.hasErrors())
            return "/auth/registration";

        authFacade.register(person);
        return "auth/confirmation";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable @NonNull String code) {
        log.info("activation code: {}", code);
        if (authFacade.activatePerson(code))
            return "redirect:/login";

        return "auth/wrong_code";
    }

    @GetMapping("/hello")
    public String testYourIq(Model model) {
        model.addAttribute("testYourIq", authFacade.testYourIq());
        return "auth/hello";
    }

    @PostMapping("/hello")
    public String testYourIq(@ModelAttribute("testYourIq") @Valid @NonNull TestYourIq testYourIq,
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
}