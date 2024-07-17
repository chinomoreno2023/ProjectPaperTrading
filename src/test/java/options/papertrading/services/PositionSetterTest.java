package options.papertrading.services;

import options.papertrading.dto.option.OptionDto;
import options.papertrading.models.option.Option;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PositionSetterTest {

    @Autowired
    private PositionSetter positionSetter;

    @Autowired
    private PersonsService personsService;

    private Portfolio newPortfolio = new Portfolio();
    private Portfolio oldPortfolio = new Portfolio();
    private OptionDto optionDto = new OptionDto();
    private Person owner = new Person();
    private Option option = new Option();

    @BeforeEach
    void setUp() {
        owner.setOpenLimit(1000.0);

        option.setBuyCollateral(300.0);
        option.setWriteCollateral(300.0);
        option.setPrice(200.0);
        option.setStepPrice(0.01);

        newPortfolio.setOwner(owner);
        newPortfolio.setOption(option);
        newPortfolio.setVolume(3);
        newPortfolio.setTradePrice(150.0);

        oldPortfolio.setVolume(1);
        oldPortfolio.setOption(option);
        oldPortfolio.setOwner(owner);

        optionDto.setBuyOrWrite(1);
    }

//    @Test
//    void isThereEnoughMoneyForBuy() {
//        assertThat(positionSetter.isThereEnoughMoneyForBuyForTest(newPortfolio)).isTrue();
//
//        newPortfolio.getOption().setBuyCollateral(334);
//        assertThat(positionSetter.isThereEnoughMoneyForBuyForTest(newPortfolio)).isFalse();
//    }
//
//    @Test
//    void isThereEnoughMoneyForWrite() {
//        assertThat(positionSetter.isThereEnoughMoneyForWriteForTest(newPortfolio)).isTrue();
//
//        newPortfolio.getOption().setWriteCollateral(334);
//        assertThat(positionSetter.isThereEnoughMoneyForWriteForTest(newPortfolio)).isFalse();
//    }

//    @Test
//    void isContained() {
//        Person person = personsService.findByEmail("admin@mail.ru").get();
//        newPortfolio.getOption().setId("SF600BC4");
//        newPortfolio.setOwner(person);
//
//        assertThat(positionSetter.isContained(newPortfolio)).isTrue();
//
//        newPortfolio.getOption().setId("SF600BC3");
//        assertThat(positionSetter.isContained(newPortfolio)).isFalse();
//    }



    @Test
    void checkBuyOrWrite() {
        assertThat(positionSetter.checkBuyOrWrite(optionDto)).isTrue();

        optionDto.setBuyOrWrite(-1);
        assertThat(positionSetter.checkBuyOrWrite(optionDto)).isFalse();

        optionDto.setBuyOrWrite(0);
        assertThatThrownBy(() -> positionSetter.checkBuyOrWrite(optionDto)).isInstanceOf(IllegalArgumentException.class);

        optionDto.setBuyOrWrite(2);
        assertThatThrownBy(() -> positionSetter.checkBuyOrWrite(optionDto)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void checkDirectOrReverse() {
        assertThat(positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)).isTrue();

        optionDto.setBuyOrWrite(-1);
        assertThat(positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)).isFalse();

        oldPortfolio.setVolume(-1);
        assertThat(positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)).isTrue();

        optionDto.setBuyOrWrite(1);
        assertThat(positionSetter.checkDirectOrReverse(optionDto, oldPortfolio)).isFalse();
    }

//    @Test
//    void isThereEnoughMoneyForReverseBuy() {
//        assertThat(positionSetter.isThereEnoughMoneyForReverseBuyForTest(oldPortfolio, newPortfolio)).isTrue();
//
//        newPortfolio.setVolume(10);
//        assertThat(positionSetter.isThereEnoughMoneyForReverseBuyForTest(oldPortfolio, newPortfolio)).isFalse();
//    }
//
//    @Test
//    void isThereEnoughMoneyForReverseWrite() {

//        assertThat(positionSetter.isThereEnoughMoneyForReverseWriteForTest(oldPortfolio, newPortfolio)).isTrue();
//
//        newPortfolio.setVolume(10);
//        assertThat(positionSetter.isThereEnoughMoneyForReverseWriteForTest(oldPortfolio, newPortfolio)).isFalse();
//    }
}