package options.papertrading.facade.interfaces;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.portfolio.Portfolio;
import java.util.List;

public interface IJournalFacade {
    List<JournalDto> showJournal();
    void addJournal(Portfolio portfolio, OptionDto optionDto);
    void clearJournal();
}