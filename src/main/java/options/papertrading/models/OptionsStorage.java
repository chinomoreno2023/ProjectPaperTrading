package options.papertrading.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OptionsStorage {
    private static List<Option> optionsList = new ArrayList<>();
    private static OptionCreater optionCreater = new OptionCreater();

    @Autowired
    public OptionsStorage() {
        optionsList = optionCreater.parseCSV(optionCreater.getPathSPY());

    }

    public static List<Option> getOptionsList() {
        return optionsList;
    }
}
