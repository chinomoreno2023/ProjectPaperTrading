package options.papertrading.controllers;

import lombok.AllArgsConstructor;
import options.papertrading.facade.AuthFacade;
import options.papertrading.models.users.Person;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person) {
        return "auth/registration";
    }

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
}