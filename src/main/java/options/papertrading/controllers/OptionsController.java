package options.papertrading.controllers;

import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.security.PersonDetails;
import options.papertrading.services.OptionsService;
import options.papertrading.services.PortfoliosService;
import options.papertrading.util.validators.VolumeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
@RequestMapping("/options")
public class OptionsController {
    private final OptionsService optionsService;
    private final VolumeValidator volumeValidator;
    private final PortfoliosService portfoliosService;

    @Autowired
    public OptionsController(OptionsService optionsService, VolumeValidator volumeValidator, PortfoliosService portfoliosService) {
        this.optionsService = optionsService;
        this.volumeValidator = volumeValidator;
        this.portfoliosService = portfoliosService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("options", optionsService.findAll());
        return "options/index";
    }

    @GetMapping("/buy/{id}")
    public String buyOption(Model model, @PathVariable("id") String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        model.addAttribute("person", personDetails.getPerson());
        model.addAttribute("option", optionsService.findOne(id));
        model.addAttribute("portfolio", new Portfolio());
        return "options/buy";
    }

    @GetMapping("/write/{id}")
    public String writeOption(Model model, @PathVariable("id") String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        model.addAttribute("person", personDetails.getPerson());
        model.addAttribute("option", optionsService.findOne(id));
        model.addAttribute("portfolio", new Portfolio());
        return "options/write";
    }

    @PostMapping("/added-buy")
    public String buyOption(@ModelAttribute("portfolio") @Valid Portfolio portfolio, BindingResult bindingResult) {
        volumeValidator.validate(portfolio, bindingResult);
        if (bindingResult.hasErrors())
            return "redirect:/options/buy/" + portfolio.getOption().getId();
        portfoliosService.save(portfolio);
        return "redirect:/portfolio";
    }

    @PostMapping("/added-write")
    public String writeOption(@ModelAttribute("portfolio") @Valid Portfolio portfolio, BindingResult bindingResult) {
        volumeValidator.validate(portfolio, bindingResult);
        if (bindingResult.hasErrors())
            return "redirect:/options/write/" + portfolio.getOption().getId();
        portfolio.setVolume(portfolio.getVolume()*-1);
        portfoliosService.save(portfolio);
        return "redirect:/portfolio";
    }
}