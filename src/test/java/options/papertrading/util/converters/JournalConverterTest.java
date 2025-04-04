package options.papertrading.util.converters;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import options.papertrading.util.mappers.JournalMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalConverterTest {

    @Mock
    private JournalMapper journalMapper;

    @InjectMocks
    private JournalConverter journalConverter;

    @Test
    void convertJournalToJournalDto() {
        when(journalMapper.convertToJournalDto(any(Journal.class))).thenReturn(new JournalDto());

        JournalDto journalDto = journalConverter.convertJournalToJournalDto(new Journal());

        assertAll(
                () -> verify(journalMapper, times(1)).convertToJournalDto(any(Journal.class)),
                () -> assertNotNull(journalDto)
        );
    }

    @Test
    void convertJournalToJournalDto_ShouldThrow_WhenInputIsNull() {
        Journal journal = null;

        assertThrows(NullPointerException.class,
                        () -> journalConverter.convertJournalToJournalDto(journal));

    }

    @Test
    void convertJournalListToJournalDtoList() {
        int size = ThreadLocalRandom.current().nextInt(3, 11);
        List<Journal> journals = IntStream.range(0, size)
                .mapToObj(i -> {
                    Journal journal = new Journal();
                    journal.setDateAndTime(generateRandomDateTime());
                    journal.setOwner(new Person());
                    return journal;
                })
                .collect(toList());

        when(journalMapper.convertToJournalDto(any())).thenAnswer(invocation -> {
            Journal inputJournal = invocation.getArgument(0);
            JournalDto dto = new JournalDto();
            dto.setDateAndTime(inputJournal.getDateAndTime());
            return dto;
        });

        List<JournalDto> result = journalConverter.convertJournalListToJournalDtoList(journals);

        assertAll(
                () -> verify(journalMapper, times(size)).convertToJournalDto(any()),
                () -> assertNotNull(result),
                () -> assertFalse(result.contains(null)),
                () -> assertEquals(size, result.size()),
                () -> assertTrue(isSortedDesc(result)),
                () -> assertFalse(result.contains(null))
        );
    }

    private LocalDateTime generateRandomDateTime() {
        long minDay = LocalDateTime.of(2000, 1, 1, 0, 0)
                .toEpochSecond(java.time.ZoneOffset.UTC);
        long maxDay = LocalDateTime.of(2030, 12, 31, 23, 59)
                .toEpochSecond(java.time.ZoneOffset.UTC);
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

        return LocalDateTime.ofEpochSecond(randomDay, 0, java.time.ZoneOffset.UTC);
    }

    private boolean isSortedDesc(List<JournalDto> list) {
        return IntStream.range(0, list.size() - 1)
                .noneMatch(i -> list.get(i).getDateAndTime()
                        .isBefore(list.get(i + 1).getDateAndTime()));
    }

    @Test
    void convertJournalListToJournalDtoList_ShouldThrow_WhenInputIsNull() {
        List<Journal> journals = null;

        assertThrows(NullPointerException.class,
                        () -> journalConverter.convertJournalListToJournalDtoList(journals));
    }
}