package options.papertrading.facade.json;

import lombok.AllArgsConstructor;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.interfaces.IJsonJournalFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class JsonJournalFacade{
    private final IJsonJournalFacade journalService;

    public List<JournalDto> showJournal() {
        return journalService.showJournal();
    }
}
