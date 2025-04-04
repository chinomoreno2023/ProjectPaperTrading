package options.papertrading.util.converters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class TextConverterTest {
    private final TextConverter textConverter = new TextConverter();

    @Test
    void readMailTextFromFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        String expectedText = "Some text";
        Files.write(file, expectedText.getBytes());

        String result = textConverter.readMailTextFromFile(file.toString());

        assertEquals(expectedText, result);
    }

    @Test
    void readMailTextFromFile_shouldThrowExceptionForMissingFile() {
        assertThrows(IOException.class, () -> {
            textConverter.readMailTextFromFile("non_existent_file.txt");
        });
    }
}