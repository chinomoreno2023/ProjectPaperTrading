package options.papertrading.controllers;

import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.models.users.Person;
import options.papertrading.security.PersonDetails;
import options.papertrading.services.PortfoliosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfoliosService portfoliosService;

    @Autowired
    public PortfolioController(PortfoliosService portfoliosService) {
        this.portfoliosService = portfoliosService;
    }

    @GetMapping()
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();
        List<Portfolio> portfolios = portfoliosService.indexWithJoin();
        List<Portfolio> sortedPortfolios = new ArrayList<>();
        for (Portfolio item : portfolios)
            if (item.getOwner().getId() == person.getId())
            sortedPortfolios.add(item);
        model.addAttribute("portfolios", sortedPortfolios);
        return "portfolio/index";
    }

    @GetMapping("/delete/{id}")
    public String confirmDeletion(Model model, @PathVariable("id") int id) {
        model.addAttribute("portfolio", portfoliosService.findOne(id));
        return "portfolio/delete";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        portfoliosService.delete(id);
        return "redirect:/portfolio";
    }
}
