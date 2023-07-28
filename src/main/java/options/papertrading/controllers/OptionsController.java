package options.papertrading.controllers;




//package options.papertrading.controllers;
//
//import options.papertrading.models.options.Option;
//import options.papertrading.models.options.OptionsStorage;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import java.util.UUID;
//
//@Controller
//public class OptionsController {
//
//    @GetMapping("/")
//    public String showOptionsList(Model model) {
//        model.addAttribute("options", OptionsStorage.getOptionsList());
//        return "options-list";
//    }
//
//    @GetMapping("/create-form")
//    public String create() {
//        return "create-form";
//    }
//
//    @PostMapping("/create")
//    public String addOption(Option option) {
//        option.setId(UUID.randomUUID().toString());
//        OptionsStorage.getOptionsList().add(option);
//        return "redirect:/";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String delete(@PathVariable("id") String id) {
//        Option optionToDelete = OptionsStorage.getOptionsList().stream()
//                .filter(option -> option.getId().equals(id))
//                .findFirst()
//                .orElseThrow(RuntimeException::new);
//        OptionsStorage.getOptionsList().remove(optionToDelete);
//        return "redirect:/";
//    }
//
//    @GetMapping("/edit-form/{id}")
//    public String createform(@PathVariable("id") String id, Model model) {
//        Option optionToEdit = OptionsStorage.getOptionsList().stream()
//                .filter(item -> item.getId().equals(id))
//                .findFirst().orElseThrow(RuntimeException::new);
//        model.addAttribute("option", optionToEdit);
//        return "edit-form";
//    }
//
//    @PostMapping("/update")
//    public String update(Option option) {
//        delete(option.getId());
//        OptionsStorage.getOptionsList().add(option);
//        return "redirect:/";
//    }
//}

import jakarta.validation.Valid;
import options.papertrading.dao.OptionDao;
import options.papertrading.dao.PortfolioDao;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.util.VolumeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/options")
public class OptionsController {
    private final OptionDao optionDao;
    private final PortfolioDao portfolioDao;
    private final VolumeValidator volumeValidator;

    @Autowired
    public OptionsController(OptionDao optionDao, PortfolioDao portfolioDao, VolumeValidator volumeValidator) {
        this.optionDao = optionDao;
        this.portfolioDao = portfolioDao;
        this.volumeValidator = volumeValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("options", optionDao.index());
        return "options/index";
    }
/**
 * Нужно будет загружать по portfolios.id список опционов конкретного юзера.
 * То есть нужно передавать еще и portfolios.id
 */
    @GetMapping("/buy/{optionId}")
    public String buyOption(Model model, @PathVariable("optionId") String optionId) {
        model.addAttribute("option", optionDao.findOptionById(optionId));
        model.addAttribute("portfolio", new Portfolio());
        return "options/buy";
    }

    @GetMapping("/write/{optionId}")
    public String writeOption(Model model, @PathVariable("optionId") String optionId) {
        model.addAttribute("option", optionDao.findOptionById(optionId));
        model.addAttribute("portfolio", new Portfolio());
        return "options/write";
    }

//    @PostMapping("/added-buy")
//    public String buyOption(@ModelAttribute("portfolio") @Valid Portfolio portfolio, BindingResult bindingResult) {
//        String id = portfolio.getOptionId();
//        volumeValidator.validate(portfolio, bindingResult);
//        if (bindingResult.hasErrors())
//            return "redirect:/options/buy/" + id;
//        optionDao.addOption(portfolio);
//        return "redirect:/portfolio";
//    }

//    @PostMapping("/added-write")
//    public String writeOption(@ModelAttribute("portfolio") @Valid Portfolio portfolio, BindingResult bindingResult) {
//        String id = portfolio.getOptionId();
//        volumeValidator.validate(portfolio, bindingResult);
//        if (bindingResult.hasErrors())
//            return "redirect:/options/write/" + id;
//        portfolio.setVolume(portfolio.getVolume()*-1);
//        optionDao.addOption(portfolio);
//        return "redirect:/portfolio";
//    }
}