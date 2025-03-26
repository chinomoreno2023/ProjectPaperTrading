package options.papertrading.util.converters;

import options.papertrading.dto.journal.JournalDto;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.person.Person;
import options.papertrading.util.mappers.JournalMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JournalConverterDiffblueTest {
    /**
     * Test {@link JournalConverter#convertJournalToJournalDto(Journal)}.
     * <ul>
     *   <li>Then return DateAndTime toLocalTime toString is {@code 00:00}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JournalConverter#convertJournalToJournalDto(Journal)}
     */
    @Test
    @DisplayName("Test convertJournalToJournalDto(Journal); then return DateAndTime toLocalTime toString is '00:00'")
    @Tag("MaintainedByDiffblue")
    void testConvertJournalToJournalDto_thenReturnDateAndTimeToLocalTimeToStringIs0000() {
        // Arrange
        JournalConverter journalConverter = new JournalConverter(JournalMapper.INSTANCE);

        Person owner = new Person();
        owner.setActivationCode("Activation Code");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Role");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        LocalDate ofResult = LocalDate.of(1970, 1, 1);
        journal.setDateAndTime(ofResult.atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Option Name");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        // Act
        JournalDto actualConvertJournalToJournalDtoResult = journalConverter.convertJournalToJournalDto(journal);

        // Assert
        LocalDateTime dateAndTime = actualConvertJournalToJournalDtoResult.getDateAndTime();
        assertEquals("00:00", dateAndTime.toLocalTime().toString());
        LocalDate toLocalDateResult = dateAndTime.toLocalDate();
        assertEquals("1970-01-01", toLocalDateResult.toString());
        assertEquals("Option Name", actualConvertJournalToJournalDtoResult.getOptionName());
        assertEquals(1, actualConvertJournalToJournalDtoResult.getVolume());
        assertEquals(10.0d, actualConvertJournalToJournalDtoResult.getPrice());
        assertEquals(19088743, actualConvertJournalToJournalDtoResult.getBuyOrWrite());
        assertSame(ofResult, toLocalDateResult);
    }

    /**
     * Test {@link JournalConverter#convertJournalListToJournalDtoList(List)}.
     * <ul>
     *   <li>Then return size is one.</li>
     * </ul>
     * <p>
     * Method under test: {@link JournalConverter#convertJournalListToJournalDtoList(List)}
     */
    @Test
    @DisplayName("Test convertJournalListToJournalDtoList(List); then return size is one")
    @Tag("MaintainedByDiffblue")
    void testConvertJournalListToJournalDtoList_thenReturnSizeIsOne() {
        // Arrange
        JournalConverter journalConverter = new JournalConverter(JournalMapper.INSTANCE);

        Person owner = new Person();
        owner.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        journal.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        ArrayList<Journal> journalList = new ArrayList<>();
        journalList.add(journal);

        // Act
        List<JournalDto> actualConvertJournalListToJournalDtoListResult = journalConverter
                .convertJournalListToJournalDtoList(journalList);

        // Assert
        assertEquals(1, actualConvertJournalListToJournalDtoListResult.size());
        JournalDto getResult = actualConvertJournalListToJournalDtoListResult.get(0);
        assertEquals(1, getResult.getVolume());
        assertEquals(10.0d, getResult.getPrice());
        assertEquals(19088743, getResult.getBuyOrWrite());
    }

    /**
     * Test {@link JournalConverter#convertJournalListToJournalDtoList(List)}.
     * <ul>
     *   <li>Then return size is three.</li>
     * </ul>
     * <p>
     * Method under test: {@link JournalConverter#convertJournalListToJournalDtoList(List)}
     */
    @Test
    @DisplayName("Test convertJournalListToJournalDtoList(List); then return size is three")
    @Tag("MaintainedByDiffblue")
    void testConvertJournalListToJournalDtoList_thenReturnSizeIsThree() {
        // Arrange
        JournalConverter journalConverter = new JournalConverter(JournalMapper.INSTANCE);

        Person owner = new Person();
        owner.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        journal.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        Person owner2 = new Person();
        owner2.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(0.5d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(0.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Converting {} to journalDto. Result: {}");
        owner2.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner2.setUsername("Converting {} to journalDto. Result: {}");

        Journal journal2 = new Journal();
        journal2.setBuyOrWrite(1);
        journal2.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal2.setId(2L);
        journal2.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal2.setOwner(owner2);
        journal2.setPrice(0.5d);
        journal2.setVolume(0);

        Person owner3 = new Person();
        owner3.setActivationCode("Activation Code");
        owner3.setActive(true);
        owner3.setCurrentNetPosition(-0.5d);
        owner3.setEmail("prof.einstein@example.org");
        owner3.setId(3L);
        owner3.setJournal(new ArrayList<>());
        owner3.setOpenLimit(-0.5d);
        owner3.setOptionsInPortfolio(new ArrayList<>());
        owner3.setPassword("Converting journal list '{}' to journalDto list. Result: {} ");
        owner3.setRole("Role");
        owner3.setUsername("Converting journal list '{}' to journalDto list. Result: {} ");

        Journal journal3 = new Journal();
        journal3.setBuyOrWrite(0);
        journal3.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal3.setId(3L);
        journal3.setOptionName("Option Name");
        journal3.setOwner(owner3);
        journal3.setPrice(-0.5d);
        journal3.setVolume(-1);

        ArrayList<Journal> journalList = new ArrayList<>();
        journalList.add(journal3);
        journalList.add(journal2);
        journalList.add(journal);

        // Act
        List<JournalDto> actualConvertJournalListToJournalDtoListResult = journalConverter
                .convertJournalListToJournalDtoList(journalList);

        // Assert
        assertEquals(3, actualConvertJournalListToJournalDtoListResult.size());
        JournalDto getResult = actualConvertJournalListToJournalDtoListResult.get(2);
        assertEquals("Converting journal list '{}' to journalDto list. Result: {} ", getResult.getOptionName());
        JournalDto getResult2 = actualConvertJournalListToJournalDtoListResult.get(0);
        assertEquals("Option Name", getResult2.getOptionName());
        assertEquals(-0.5d, getResult2.getPrice());
        assertEquals(-1, getResult2.getVolume());
        assertEquals(0, getResult2.getBuyOrWrite());
        JournalDto getResult3 = actualConvertJournalListToJournalDtoListResult.get(1);
        assertEquals(0, getResult3.getVolume());
        assertEquals(0.5d, getResult3.getPrice());
        assertEquals(1, getResult3.getBuyOrWrite());
        assertEquals(1, getResult.getVolume());
        assertEquals(10.0d, getResult.getPrice());
        assertEquals(19088743, getResult.getBuyOrWrite());
    }

    /**
     * Test {@link JournalConverter#convertJournalListToJournalDtoList(List)}.
     * <ul>
     *   <li>Then return size is two.</li>
     * </ul>
     * <p>
     * Method under test: {@link JournalConverter#convertJournalListToJournalDtoList(List)}
     */
    @Test
    @DisplayName("Test convertJournalListToJournalDtoList(List); then return size is two")
    @Tag("MaintainedByDiffblue")
    void testConvertJournalListToJournalDtoList_thenReturnSizeIsTwo() {
        // Arrange
        JournalConverter journalConverter = new JournalConverter(JournalMapper.INSTANCE);

        Person owner = new Person();
        owner.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setActive(true);
        owner.setCurrentNetPosition(10.0d);
        owner.setEmail("jane.doe@example.org");
        owner.setId(1L);
        owner.setJournal(new ArrayList<>());
        owner.setOpenLimit(10.0d);
        owner.setOptionsInPortfolio(new ArrayList<>());
        owner.setPassword("iloveyou");
        owner.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner.setUsername("janedoe");

        Journal journal = new Journal();
        journal.setBuyOrWrite(19088743);
        journal.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal.setId(1L);
        journal.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal.setOwner(owner);
        journal.setPrice(10.0d);
        journal.setVolume(1);

        Person owner2 = new Person();
        owner2.setActivationCode("Converting journal list '{}' to journalDto list. Result: {} ");
        owner2.setActive(false);
        owner2.setCurrentNetPosition(0.5d);
        owner2.setEmail("john.smith@example.org");
        owner2.setId(2L);
        owner2.setJournal(new ArrayList<>());
        owner2.setOpenLimit(0.5d);
        owner2.setOptionsInPortfolio(new ArrayList<>());
        owner2.setPassword("Converting {} to journalDto. Result: {}");
        owner2.setRole("Converting journal list '{}' to journalDto list. Result: {} ");
        owner2.setUsername("Converting {} to journalDto. Result: {}");

        Journal journal2 = new Journal();
        journal2.setBuyOrWrite(1);
        journal2.setDateAndTime(LocalDate.of(1970, 1, 1).atStartOfDay());
        journal2.setId(2L);
        journal2.setOptionName("Converting journal list '{}' to journalDto list. Result: {} ");
        journal2.setOwner(owner2);
        journal2.setPrice(0.5d);
        journal2.setVolume(0);

        ArrayList<Journal> journalList = new ArrayList<>();
        journalList.add(journal2);
        journalList.add(journal);

        // Act
        List<JournalDto> actualConvertJournalListToJournalDtoListResult = journalConverter
                .convertJournalListToJournalDtoList(journalList);

        // Assert
        assertEquals(2, actualConvertJournalListToJournalDtoListResult.size());
        JournalDto getResult = actualConvertJournalListToJournalDtoListResult.get(0);
        assertEquals(0, getResult.getVolume());
        assertEquals(0.5d, getResult.getPrice());
        assertEquals(1, getResult.getBuyOrWrite());
        JournalDto getResult2 = actualConvertJournalListToJournalDtoListResult.get(1);
        assertEquals(1, getResult2.getVolume());
        assertEquals(10.0d, getResult2.getPrice());
        assertEquals(19088743, getResult2.getBuyOrWrite());
    }

    /**
     * Test {@link JournalConverter#convertJournalListToJournalDtoList(List)}.
     * <ul>
     *   <li>When {@link ArrayList#ArrayList()}.</li>
     *   <li>Then return Empty.</li>
     * </ul>
     * <p>
     * Method under test: {@link JournalConverter#convertJournalListToJournalDtoList(List)}
     */
    @Test
    @DisplayName("Test convertJournalListToJournalDtoList(List); when ArrayList(); then return Empty")
    @Tag("MaintainedByDiffblue")
    void testConvertJournalListToJournalDtoList_whenArrayList_thenReturnEmpty() {
        // Arrange
        JournalConverter journalConverter = new JournalConverter(JournalMapper.INSTANCE);

        // Act and Assert
        assertTrue(journalConverter.convertJournalListToJournalDtoList(new ArrayList<>()).isEmpty());
    }
}
