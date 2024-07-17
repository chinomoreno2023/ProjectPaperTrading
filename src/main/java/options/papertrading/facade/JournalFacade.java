package options.papertrading.facade;

import lombok.AllArgsConstructor;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import org.springframework.stereotype.Component;
import java.util.List;

@AllArgsConstructor
@Component
public class JournalFacade {
    private final IJournalFacade journalFacade;

    public List<JournalDto> showJournal() {
        return journalFacade.showJournal();
    }

    public void clearJournal() {
        journalFacade.clearJournal();
    }
}