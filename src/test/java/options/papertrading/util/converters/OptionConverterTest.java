package options.papertrading.util.converters;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import options.papertrading.services.processors.interfaces.IPositionSetter;
import options.papertrading.util.mappers.OptionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionConverterTest {

    @Mock
    private IPositionSetter positionSetter;

    @Mock
    private OptionMapper optionMapper;

    @InjectMocks
    private OptionConverter optionConverter;

    @Test
    void convertToOptionDto() {
        OptionDto optionDto = new OptionDto();
        when(optionMapper.convertToOptionDto(any())).thenReturn(optionDto);
        when(positionSetter.countPriceInRur(any(Option.class))).thenReturn(1.0);

        OptionDto result = optionConverter.convertToOptionDto(new Option());

        assertAll(
                () -> verify(optionMapper).convertToOptionDto(any()),
                () -> verify(positionSetter).countPriceInRur(any(Option.class)),
                () -> assertEquals(optionDto, result),
                () -> assertEquals(1.0, result.getPrice(), 0.001)
        );
    }

    @Test
    void convertToOptionDto_ShouldThrow_WheInputIsNull() {
        Option option = null;

         assertThrows(NullPointerException.class,
                        () -> optionConverter.convertToOptionDto(option));

    }

    @Test
    void convertListToOptionDtoList() {
        int optionsAmount = ThreadLocalRandom.current().nextInt(3, 11);
        List<Option> options = IntStream.range(0, optionsAmount)
                .mapToObj(o -> {
                    Option option = new Option();
                    option.setPrice(9.0);
                    option.setPortfolio(new ArrayList<>());
                    return option;
                })
                .collect(toList());

        when(optionMapper.convertToOptionDto(any(Option.class))).thenReturn(new OptionDto());
        when(positionSetter.countPriceInRur(any(Option.class))).thenReturn(9.0);

        List<OptionDto> result = optionConverter.convertListToOptionDtoList(options);

        assertAll(
                () -> verify(optionMapper, times(optionsAmount)).convertToOptionDto(any()),
                () -> verify(positionSetter, times(optionsAmount)).countPriceInRur(any(Option.class)),
                () -> assertNotNull(result),
                () -> assertFalse(result.contains(null)),
                () -> assertEquals(optionsAmount, result.size()),
                () -> assertTrue(result.stream().allMatch(dto -> dto.getPrice() == 9.0))
        );
    }

    @Test
    void convertListToOptionDtoList_SouldThrow_WheInputIsNull() {
        List<Option> options = null;

        assertThrows(NullPointerException.class,
                        () -> optionConverter.convertListToOptionDtoList(options));
    }

    @Test
    void createOptionDtoFromView() {
        String id = "123";
        int volume = 7;
        int buyOrWrite = 1;
        OptionDto optionDto = optionConverter.createOptionDtoFromView(id, volume, buyOrWrite);
        assertAll(
                () -> assertEquals(id, optionDto.getId()),
                () -> assertEquals(volume, optionDto.getVolume()),
                () -> assertEquals(buyOrWrite, optionDto.getBuyOrWrite())
        );
    }

    @Test
    void createOptionDtoFromView_ShouldThrow_WheInputIsNull() {
        String id = null;
        int volume = 7;
        int buyOrWrite = 1;

        assertThrows(NullPointerException.class,
                () -> optionConverter.createOptionDtoFromView(id, volume, buyOrWrite));
    }
}