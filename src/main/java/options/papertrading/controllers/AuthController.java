package options.papertrading.controllers;

import lombok.AllArgsConstructor;
import options.papertrading.facade.AuthFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.TestYourIq;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthFacade authFacade;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

//    @GetMapping("/registration")
//    public String registrationPage(@ModelAttribute("person") Person person) {
//        return "auth/registration";
//    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        authFacade.validate(person, bindingResult);
        if (bindingResult.hasErrors())
            return "/auth/registration";

        authFacade.register(person);
        return "auth/confirmation";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code) {
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
    public String testYourIq(@ModelAttribute("testYourIq") @Valid TestYourIq testYourIq,
                             BindingResult bindingResult,
                             @ModelAttribute("person") Person person) {
        authFacade.testYourIqValidate(testYourIq, bindingResult);
        if (bindingResult.hasErrors())
            return "auth/hello";

        return "auth/registration";
    }
}