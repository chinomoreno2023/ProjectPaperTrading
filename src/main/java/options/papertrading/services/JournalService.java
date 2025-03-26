package options.papertrading.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.util.converters.JournalConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalService implements IJournalFacade {
    private final JournalRepository journalRepository;
    private final IPersonFacadeHtmlVersion personsService;
    private final IPositionSetter positionSetter;
    private final JournalConverter journalConverter;

    @Transactional(readOnly = true)
    public List<JournalDto> showJournal() {
        Person owner = personsService.getCurrentPerson();
        List<Journal> journalList = journalRepository.findAllByOwner(owner);
        return journalConverter.convertJournalListToJournalDtoList(journalList);
    }

    @Transactional
    public void addJournal(@NonNull Portfolio portfolio, @NonNull OptionDto optionDto) {
        Journal journal = new Journal();
        journal.setDateAndTime(LocalDateTime.now());
        journal.setOptionName(portfolio.getOption().getId());
        journal.setVolume(optionDto.getVolume());
        journal.setPrice(positionSetter.countPriceInRur(portfolio));
        journal.setBuyOrWrite(optionDto.getBuyOrWrite());
        journal.setOwner(portfolio.getOwner());
        journalRepository.save(journal);
        log.info("Adding Journal '{}' to DB", journal);
    }

    @Transactional
    public void clearJournal() {
        Person owner = personsService.getCurrentPerson();
        journalRepository.deleteAllByOwner(owner);
    }
}