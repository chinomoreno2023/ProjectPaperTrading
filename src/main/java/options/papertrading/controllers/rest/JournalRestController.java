package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("rest/journal")
@AllArgsConstructor
public class JournalRestController {
    private final IJournalFacade jsonJournalFacade;

    @GetMapping
    public ResponseEntity<List<JournalDto>> showJournal() {
        List<JournalDto> journalDtoList = jsonJournalFacade.showJournal();
        log.info("Request to show journal. Result: {}", journalDtoList);
        return journalDtoList.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(journalDtoList);
    }

    @PostMapping("/clear")
    public ResponseEntity<String> clearJournal() {
        try {
            jsonJournalFacade.clearJournal();
            return ResponseEntity.ok("Journal cleared successfully");
        }
        catch (Exception exception) {
            log.info("Request to clear journal. Exception: {}", exception.getMessage());
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }
    }
}