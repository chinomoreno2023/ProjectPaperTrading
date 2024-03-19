package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.json.JsonJournalFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/journal")
@AllArgsConstructor
public class JournalRestController {
    private final JsonJournalFacade jsonJournalFacade;

    @GetMapping
    public ResponseEntity<List<JournalDto>> showJournal() {
        List<JournalDto> journalDtoList = jsonJournalFacade.showJournal();
        log.info("Request to show journal. Result: {}", journalDtoList);
        return journalDtoList.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(journalDtoList, HttpStatus.OK);
    }
}