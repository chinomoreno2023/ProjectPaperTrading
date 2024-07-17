package options.papertrading.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.OptionFacade;
import options.papertrading.facade.PersonFacade;
import options.papertrading.facade.PortfolioFacade;
import options.papertrading.models.person.Person;
import options.papertrading.util.exceptions.InsufficientFundsException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioController {
    private final PortfolioFacade portfolioFacade;
    private final OptionFacade optionFacade;
    private final PersonFacade personFacade;

    @GetMapping
    public String showAllPortfolios(Model model) {
        final Person owner = personFacade.getCurrentPerson();
        if (owner.getActivationCode() != null) {
            owner.setActivationCode(null);
            personFacade.save(owner);
            return "memo/memoForRegistered";
        }

        List<PortfolioDto> portfolios = portfolioFacade.showAllPortfolios();
        List<OptionDto> options = optionFacade.showOptionsFromCurrentPortfolios(portfolios);
        log.info("Request to show all portfolios. Result: {}", portfolios);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("options", options);
        model.addAttribute("owner", owner);
        return "portfolio/portfolio";
    }

    @GetMapping("/options")
    public String showOptionsList(@RequestParam("selectedValue") String selectedValue, Model model) {
        List<OptionDto> options = optionFacade.showOptionsListByPrefix(selectedValue);
        model.addAttribute("options", options);
        return "options/options";
    }

    @PostMapping("/confirmation")
    public ResponseEntity<String> addPortfolio(@RequestParam("id") String id,
                                               @RequestParam("volume") int volume,
                                               @RequestParam("buyOrWrite") int buyOrWrite) {
        log.info("Request to add portfolio. Id: {}, volume: {}, buyOrWrite: {}", id, volume, buyOrWrite);
        if (volume <= 0) {
            return ResponseEntity.badRequest().body("Объём не может быть равен нулю");
        }

        try {
            portfolioFacade.addPortfolio(id, volume, buyOrWrite);
            return ResponseEntity.ok("Операция успешно выполнена");
        }
        catch (InsufficientFundsException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        try {
            portfolioFacade.reset();
            return ResponseEntity.ok("Данные портфеля были сброшены");
        }
        catch (Exception exception) {
            log.info("Request to reset. Exception: {}", exception.getMessage());
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }

//    @GetMapping("/testPage")
//    public String openTestPage() {
//        return "testPage";
//    }
}