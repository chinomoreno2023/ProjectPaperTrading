package options.papertrading.controllers;

import options.papertrading.models.Option;
import options.papertrading.models.OptionsStorage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.UUID;

@Controller
public class OptionsController {

    @GetMapping("/")
    public String showOptionsList(Model model) {
        model.addAttribute("options", OptionsStorage.getOptionsList());
        return "options-list";
    }

    @GetMapping("/create-form")
    public String create() {
        return "create-form";
    }

    @PostMapping("/create")
    public String addOption(Option option) {
        option.setId(UUID.randomUUID().toString());
        OptionsStorage.getOptionsList().add(option);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        Option optionToDelete = OptionsStorage.getOptionsList().stream()
                .filter(option -> option.getId().equals(id))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        OptionsStorage.getOptionsList().remove(optionToDelete);
        return "redirect:/";
    }

    @GetMapping("/edit-form/{id}")
    public String createform(@PathVariable("id") String id, Model model) {
        Option optionToEdit = OptionsStorage.getOptionsList().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst().orElseThrow(RuntimeException::new);
        model.addAttribute("option", optionToEdit);
        return "edit-form";
    }

    @PostMapping("/update")
    public String update(Option option) {
        delete(option.getId());
        OptionsStorage.getOptionsList().add(option);
        return "redirect:/";
    }
}
