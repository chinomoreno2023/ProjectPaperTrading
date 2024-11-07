package options.papertrading.util.mappers;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.models.journal.Journal;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface JournalMapper {
    JournalMapper INSTANCE = Mappers.getMapper(JournalMapper.class);

    Journal convertToJournal(JournalDto journalDto);
    JournalDto convertToJournalDto(Journal journal);
}