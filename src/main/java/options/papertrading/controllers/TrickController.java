package options.papertrading.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequestMapping("/trick")
public class TrickController {

    @GetMapping
    public String startTrick(Model model) {
        List<Integer> numbers1 = IntStream.rangeClosed(1, 7).boxed().collect(Collectors.toList());
        List<Integer> numbers2 = IntStream.rangeClosed(8, 14).boxed().collect(Collectors.toList());
        List<Integer> numbers3 = IntStream.rangeClosed(15, 21).boxed().collect(Collectors.toList());

        Collections.shuffle(numbers1);
        Collections.shuffle(numbers2);
        Collections.shuffle(numbers3);

        int[] arr1 = IntStream.range(0, 7).map(numbers1::get).toArray();
        int[] arr2 = IntStream.range(0, 7).map(numbers2::get).toArray();
        int[] arr3 = IntStream.range(0, 7).map(numbers3::get).toArray();

        model.addAttribute("array1", arr1);
        model.addAttribute("array2", arr2);
        model.addAttribute("array3", arr3);
        return "trick/testPage";
    }
}
