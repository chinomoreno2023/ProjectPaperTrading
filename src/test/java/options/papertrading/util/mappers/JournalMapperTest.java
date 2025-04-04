package options.papertrading.util.mappers;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.models.journal.Journal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JournalMapperTest {
    private final JournalMapper journalMapper = Mappers.getMapper(JournalMapper.class);

    @Test
    void convertToJournal() {
        JournalDto journalDto = new JournalDto();
        journalDto.setDateAndTime(LocalDateTime.of(2023, 8, 1, 0, 0));
        journalDto.setOptionName("Test Option");
        journalDto.setVolume(10);
        journalDto.setPrice(100.0);
        journalDto.setBuyOrWrite(1);

        Journal journal = journalMapper.convertToJournal(journalDto);

        assertAll(
                () -> assertNotNull(journal),
                () -> assertEquals(0.0, journal.getId(), 0.001),
                () -> assertNull(journal.getOwner()),
                () -> assertEquals(LocalDateTime.of(2023, 8, 1, 0, 0),
                        journal.getDateAndTime()),
                () -> assertEquals("Test Option", journal.getOptionName()),
                () -> assertEquals(10, journal.getVolume()),
                () -> assertEquals(100.0, journal.getPrice(), 0.001),
                () -> assertEquals(1, journal.getBuyOrWrite())
        );
    }

    @Test
    void convertToJournal_shouldReturnNullAndDoesNotThrow_whenInputIsNull() {
        JournalDto journalDto = null;

        Journal journal = journalMapper.convertToJournal(journalDto);

        assertNull(journal);
    }

    @Test
    void convertToJournalDto() {
        Journal journal = new Journal();
        journal.setId(1L);
        journal.setDateAndTime(LocalDateTime.of(2023, 8, 1, 0, 0));
        journal.setOptionName("Test Option");
        journal.setVolume(10);
        journal.setPrice(100.0);
        journal.setBuyOrWrite(1);

        JournalDto journalDto = journalMapper.convertToJournalDto(journal);

        assertAll(
                () -> assertNotNull(journalDto),
                () -> assertEquals(LocalDateTime.of(2023, 8, 1, 0, 0),
                        journalDto.getDateAndTime()),
                () -> assertEquals("Test Option", journalDto.getOptionName()),
                () -> assertEquals(10, journalDto.getVolume()),
                () -> assertEquals(100.0, journalDto.getPrice(), 0.001),
                () -> assertEquals(1, journalDto.getBuyOrWrite())
        );
    }

    @Test
    void convertToJournalDto_shouldReturnNullAndDoesNotThrow_whenInputIsNull() {
        Journal journal = null;

        JournalDto journalDto = journalMapper.convertToJournalDto(journal);

        assertNull(journalDto);
    }
}