package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.json.JsonOptionFacade;
import options.papertrading.facade.json.JsonPortfolioFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RestController
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioRestController {
    private final JsonPortfolioFacade portfolioFacade;
    private final JsonOptionFacade optionFacade;

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> showAllPortfolios() {
        List<PortfolioDto> portfoliosDto = portfolioFacade.showAllPortfolios();
        return portfoliosDto.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(portfoliosDto, HttpStatus.OK);
    }

    @GetMapping("/options")
    public ResponseEntity<List<OptionDto>> showOptionsList() {
        List<OptionDto> optionsDto = optionFacade.showOptionsList();
        return optionsDto.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(optionsDto, HttpStatus.OK);
    }

    @PostMapping("/options")
    @Transactional
    public ResponseEntity<HttpStatus> addPortfolio(@RequestBody OptionDto optionDto, BindingResult bindingResult) {
        optionFacade.volumeValidate(optionDto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return new ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
        }
        portfolioFacade.addPortfolio(optionDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
//        Нужно перехватить exception
    }
}