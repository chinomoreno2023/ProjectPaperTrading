package options.papertrading.util.converters;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.journal.JournalDto;
import options.papertrading.models.journal.Journal;
import options.papertrading.util.mappers.JournalMapper;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JournalConverter {
    private final JournalMapper journalMapper;

    public JournalDto convertJournalToJournalDto(@NonNull Journal journal) {
        JournalDto journalDto = journalMapper.convertToJournalDto(journal);
        log.info("Converting {} to journalDto. Result: {}", journal, journalDto);
        return journalDto;
    }

    public List<JournalDto> convertJournalListToJournalDtoList(@NonNull List<Journal> journalList) {
        List<JournalDto> journalDtoList = journalList.parallelStream()
                .sorted(Comparator.comparing(Journal::getDateAndTime).reversed())
                .map(this::convertJournalToJournalDto)
                .collect(Collectors.toList());
        log.info("Converting journal list '{}' to journalDto list. Result: {} ", journalList, journalDtoList);
        return journalDtoList;
    }
}
