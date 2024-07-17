package options.papertrading.util.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class TextConverter {

    public String readMailTextFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        String mailText = new String(Files.readAllBytes(path));
        log.info("Path: {}, Mail text: {}", filePath, mailText);
        return mailText;
    }
}