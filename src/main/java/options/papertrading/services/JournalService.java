package options.papertrading.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJsonJournalFacade;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.JournalRepository;
import options.papertrading.util.mappers.JournalMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class JournalService implements IJsonJournalFacade {
    private final JournalMapper journalMapper;
    private final JournalRepository journalRepository;
    private final PersonsService personsService;

    public List<JournalDto> showJournal() {
        Person owner = personsService.getCurrentPerson();
        List<Journal> journalList = journalRepository.findAllByOwner(owner);
        List<JournalDto> journalDtoList = convertJournalListToJournalDtoList(journalList);
        return journalDtoList;
    }

    public JournalDto convertJournalToJournalDto(Journal journal) {
        JournalDto journalDto = journalMapper.convertToJournalDto(journal);
        log.info("Converting {} to journalDto. Result: {}", journal, journalDto);
        return journalDto;
    }

    public Journal convertJournalDtoToJournal(JournalDto journalDto) {
        Journal journal = journalMapper.convertToJournal(journalDto);
        log.info("Converting {} to journal. Result: {}", journalDto, journal);
        return journal;
    }

    public List<JournalDto> convertJournalListToJournalDtoList(List<Journal> journalList) {
        List<JournalDto> journalDtoList = journalList.stream()
                                                     .map(this::convertJournalToJournalDto)
                                                     .collect(Collectors.toList());
        log.info("Converting journal list '{}' to journalDto list. Result: {} ", journalList, journalDtoList);
        return journalDtoList;
    }

    public void addJournal(Portfolio portfolio, OptionDto optionDto) {
        Journal journal = new Journal();
        journal.setDateAndTime(LocalDateTime.now());
        journal.setOptionName(portfolio.getOption().getId());
        journal.setVolume(optionDto.getVolume());
        journal.setPrice(portfolio.getOption().getPrice() * portfolio.getOption().getStepPrice() * 100);
        journal.setBuyOrWrite(optionDto.getBuyOrWrite());
        journal.setOwner(portfolio.getOwner());
        journalRepository.save(journal);
        log.info("Adding Journal '{}' to DB", journal);
    }
}
