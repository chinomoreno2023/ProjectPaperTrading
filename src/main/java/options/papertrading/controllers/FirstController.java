package options.papertrading.controllers;

import options.papertrading.models.Option;
import options.papertrading.models.OptionCreater;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class FirstController {

    @GetMapping("/")
    public String index() {
        return "Options";
    }

    @GetMapping("/options1")
    public String options() throws FileNotFoundException {
        var optionCreater = new OptionCreater();
        List<Option> list = optionCreater.parseCSV(optionCreater.getPathSPY());
        return list.toString();
    }
}