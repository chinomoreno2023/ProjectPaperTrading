package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OptionsServiceTest {
    private final OptionsService optionsService;
    String optionId = "SF295BC4";
    private final int strike = 295;
    private final String type = "Call";
    private final int daysToMaturity = 102;

    @Autowired
    public OptionsServiceTest(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    @Test
    void findAll() {
        List<Option> optionList = optionsService.findAll();

        assertThat(optionList).isNotNull();
        assertThat(optionList.size()).isNotEqualTo(0);
        optionList.forEach(option -> assertThat(option).isInstanceOf(Option.class));
    }

//    @Test
//    void findByOptionId() {
//        Option option = optionsService.findByOptionId(optionId);
//
//        assertThat(option).isNotNull();
//        assertThat(option.getStrike()).isEqualTo(strike);
//        assertThat(option.getType()).isEqualTo(type);
//        assertThat(option.getPrice()).isInstanceOf(Double.class);
//        assertThat(option.getVolatility()).isInstanceOf(Double.class);
//        assertThat(option.getDaysToMaturity()).isInstanceOf(Integer.class);
//        assertThat(option.getBuyCollateral()).isInstanceOf(Double.class);
//        assertThat(option.getWriteCollateral()).isInstanceOf(Double.class);
//        assertThat(option.getStepPrice()).isInstanceOf(Double.class);
//    }

//    @Test
//    void convertToOptionDto() {
//        OptionDto optionDto = optionsService.convertToOptionDto(optionsService.findByOptionId(optionId));
//
//        assertThat(optionDto).isNotNull();
//        assertThat(optionDto.getStrike()).isEqualTo(strike);
//        assertThat(optionDto.getType()).isEqualTo(type);
//        assertThat(optionDto.getPrice()).isInstanceOf(Double.class);
//        assertThat(optionDto.getVolume()).isInstanceOf(Integer.class);
//        assertThat(optionDto.getVolatility()).isInstanceOf(Double.class);
//        assertThat(optionDto.getDaysToMaturity()).isInstanceOf(Integer.class);
//        assertThat(optionDto.getBuyCollateral()).isInstanceOf(Double.class);
//        assertThat(optionDto.getWriteCollateral()).isInstanceOf(Double.class);
//        assertThat(optionDto.getBuyOrWrite()).isInstanceOf(Integer.class);
//    }

//    @Test
//    void findByStrikeAndTypeAndDaysToMaturity() {
//        Option option = optionsService.findByStrikeAndTypeAndDaysToMaturity(strike, type, daysToMaturity);
//
//        assertThat(option).isNotNull();
//        assertThat(option.getStrike()).isEqualTo(strike);
//        assertThat(option.getType()).isEqualTo(type);
//        assertThat(option.getPrice()).isInstanceOf(Double.class);
//        assertThat(option.getVolatility()).isInstanceOf(Double.class);
//        assertThat(option.getDaysToMaturity()).isEqualTo(daysToMaturity);
//        assertThat(option.getBuyCollateral()).isInstanceOf(Double.class);
//        assertThat(option.getWriteCollateral()).isInstanceOf(Double.class);
//        assertThat(option.getStepPrice()).isInstanceOf(Double.class);
//    }

    @Test
    void convertListToOptionDtoList() {
        List<OptionDto> optionDtoList = optionsService.convertListToOptionDtoList(optionsService.findAll());

        assertThat(optionDtoList).isNotNull();
        assertThat(optionDtoList.size()).isNotEqualTo(0);
        optionDtoList.forEach(optionDto -> assertThat(optionDto).isInstanceOf(OptionDto.class));
    }
}