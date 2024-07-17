package options.papertrading.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/portfolio/memo")
public class MemoController {

    @GetMapping
    public String openMemoPage() {
        return "memo/memo";
    }

    @GetMapping("/memo-for-registered")
    public String openMemoPageForRegistered() {
        return "memo/memoForRegistered";
    }
}
