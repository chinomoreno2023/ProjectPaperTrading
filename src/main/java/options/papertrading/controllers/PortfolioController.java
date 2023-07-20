package options.papertrading.controllers;

import options.papertrading.dao.OptionDao;
import options.papertrading.dao.PortfolioDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioDao portfolioDao;

    @Autowired
    public PortfolioController(PortfolioDao portfolioDao) {
        this.portfolioDao = portfolioDao;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("portfolios", portfolioDao.index());
        return "portfolio/index";
    }
}
