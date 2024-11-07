package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.util.mappers.JournalMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class JournalService implements IJournalFacade {
    private final JournalMapper journalMapper;
    private final JournalRepository journalRepository;
    private final PersonsService personsService;
    private final PositionSetter positionSetter;

    @Transactional(readOnly = true)
    public List<JournalDto> showJournal() {
        Person owner = personsService.getCurrentPerson();
        List<Journal> journalList = journalRepository.findAllByOwner(owner);
        List<JournalDto> journalDtoList = convertJournalListToJournalDtoList(journalList);
        return journalDtoList;
    }

    public JournalDto convertJournalToJournalDto(@NonNull Journal journal) {
        JournalDto journalDto = journalMapper.convertToJournalDto(journal);
        log.info("Converting {} to journalDto. Result: {}", journal, journalDto);
        return journalDto;
    }

    public Journal convertJournalDtoToJournal(@NonNull JournalDto journalDto) {
        Journal journal = journalMapper.convertToJournal(journalDto);
        log.info("Converting {} to journal. Result: {}", journalDto, journal);
        return journal;
    }

    public List<JournalDto> convertJournalListToJournalDtoList(@NonNull List<Journal> journalList) {
        List<JournalDto> journalDtoList = journalList.stream()
                .sorted(Comparator.comparing(Journal::getDateAndTime).reversed())
                .map(this::convertJournalToJournalDto)
                .collect(Collectors.toList());
        log.info("Converting journal list '{}' to journalDto list. Result: {} ", journalList, journalDtoList);
        return journalDtoList;
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