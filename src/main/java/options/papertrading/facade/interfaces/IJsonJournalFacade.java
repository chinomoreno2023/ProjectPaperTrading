package options.papertrading.facade.interfaces;

import options.papertrading.dto.journal.JournalDto;
import java.util.List;

public interface IJsonJournalFacade {
    List<JournalDto> showJournal();
}
