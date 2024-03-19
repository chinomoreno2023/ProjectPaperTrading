package options.papertrading.services;

import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PortfoliosServiceTest {

    @Autowired
    private PortfoliosService portfoliosService;

    @Autowired
    private PersonsService personsService;

    @Autowired
    private OptionsService optionsService;

//    @Test
//    void findByOwnerAndOption() {
//        Person owner = personsService.findByEmail("admin@mail.ru").get();
//        Option option = optionsService.findByOptionId("SF450BC4");
//        Portfolio portfolio = portfoliosService.findByOwnerAndOption(owner, option);
//
//        assertThat(portfolio).isNotNull();
//        assertThat(portfolio.getOption().getId()).isEqualTo("SF450BC4");
//        assertThat(portfolio.getOwner().getEmail()).isEqualTo("admin@mail.ru");
//        assertThat(portfolio.getVolume()).isNotEqualTo(0);
//    }

//    @Test
//    void convertToPortfolioDto() {
//        Person owner = personsService.findByEmail("admin@mail.ru").get();
//        Option option = optionsService.findByOptionId("SF450BC4");
//        Portfolio portfolio = portfoliosService.findByOwnerAndOption(owner, option);
//
//        PortfolioDto portfolioDto = portfoliosService.convertToPortfolioDto(portfolio);
//
//        assertThat(portfolioDto).isNotNull();
//        System.out.println(portfolioDto);
//    }

    @Test
    void convertListToPortfolioDtoList() {
        Person owner = personsService.findByEmail("admin@mail.ru").get();
        List<Portfolio> portfolios = portfoliosService.findAllByOwner(owner);
        List<PortfolioDto> portfolioDtoList = portfoliosService.convertListToPortfolioDtoList(portfolios);

        assertThat(portfolioDtoList).isNotNull();
        System.out.println(portfolioDtoList);
    }

    @Test
    void findAllByOwner() {
        Person owner = personsService.findByEmail("admin@mail.ru").get();
        List<Portfolio> portfolios = portfoliosService.findAllByOwner(owner);

        assertThat(portfolios).isNotNull();
        System.out.println(portfolios);
    }
}