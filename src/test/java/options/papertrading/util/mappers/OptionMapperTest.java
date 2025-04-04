package options.papertrading.util.mappers;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OptionMapperTest {
    private final OptionMapper optionMapper = Mappers.getMapper(OptionMapper.class);

    @Test
    void convertToOptionDto() {
        Option option = new Option();
        option.setId("1");
        option.setStrike(100);
        option.setType("Call");
        option.setPrice(10.0);
        option.setVolatility(0.1);
        option.setDaysToMaturity(30);
        option.setBuyCollateral(100.0);
        option.setWriteCollateral(100.0);
        option.setStepPrice(0.1);
        option.setPortfolio(Collections.emptyList());

        OptionDto optionDto = optionMapper.convertToOptionDto(option);

        assertAll(
                () -> assertNotNull(optionDto),
                () -> assertEquals("1", optionDto.getId()),
                () -> assertEquals(100, optionDto.getStrike()),
                () -> assertEquals("Call", optionDto.getType()),
                () -> assertEquals(10.0, optionDto.getPrice(), 0.001),
                () -> assertEquals(0.1, optionDto.getVolatility(), 0.001),
                () -> assertEquals(30, optionDto.getDaysToMaturity()),
                () -> assertEquals(100.0, optionDto.getBuyCollateral(), 0.001),
                () -> assertEquals(100.0, optionDto.getWriteCollateral(), 0.001),
                () -> assertEquals(0.1, optionDto.getStepPrice(), 0.001),
                () -> assertEquals(0, optionDto.getVolume()),
                () -> assertEquals(0, optionDto.getBuyOrWrite())
        );
    }

    @Test
    void convertToOptionDto_shouldReturnNullAndDoesNotThrow_whenInputIsNull() {
        Option option = null;

        OptionDto optionDto = optionMapper.convertToOptionDto(option);

        assertNull(optionDto);
    }
}