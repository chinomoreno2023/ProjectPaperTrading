package options.papertrading.controllers.rest;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.json.JsonOptionFacade;
import options.papertrading.facade.json.JsonPortfolioFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/portfolio")
@AllArgsConstructor
public class PortfolioRestController {
    private final JsonPortfolioFacade portfolioFacade;
    private final JsonOptionFacade optionFacade;

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> showAllPortfolios() {
        List<PortfolioDto> portfoliosDto = portfolioFacade.showAllPortfolios();
        log.info("Request to show all portfolios. Result: {}", portfoliosDto);
        return portfoliosDto.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(portfoliosDto, HttpStatus.OK);
    }

    @GetMapping("/options")
    public ResponseEntity<List<OptionDto>> showOptionsList() {
        List<OptionDto> optionsDto = optionFacade.showOptionsList();
        log.info("Request to show options list. Result: {}", optionsDto);
        return optionsDto.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(optionsDto, HttpStatus.OK);
    }

    @PostMapping("/options")
    @Transactional
    public ResponseEntity<HttpStatus> addPortfolio(@RequestBody @NonNull OptionDto optionDto,
                                                   @NonNull BindingResult bindingResult) {
        log.info("trying to add optionDto {} to portfolio", optionDto);
        optionFacade.volumeValidate(optionDto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return new ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST);
        }
        try {
            portfolioFacade.addPortfolio(optionDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch (HttpServerErrorException exception) {
            log.error("Server error adding option to portfolio: {}", exception.getMessage(), exception);
            return new ResponseEntity("Server error adding option to portfolio", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}