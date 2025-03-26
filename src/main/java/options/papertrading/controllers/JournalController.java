package options.papertrading.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.JournalFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/portfolio/journal")
public class JournalController {
    private final JournalFacade journalFacade;

    @GetMapping
    public String showJournal(Model model) {
        List<JournalDto> journalDtoList = journalFacade.showJournal();
        log.info("Request to show journal. Result: {}", journalDtoList);
        model.addAttribute("journalList", journalDtoList);
        return "journal/journal";
    }

    @PostMapping("/clear")
    public ResponseEntity<String> clearJournal() {
        try {
            journalFacade.clearJournal();
            return ResponseEntity.ok("Вы очистили журнал");
        } catch (Exception exception) {
            log.info("Request to clear journal. Exception: {}", exception.getMessage());
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }
}
