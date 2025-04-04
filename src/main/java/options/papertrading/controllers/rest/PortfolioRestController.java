package options.papertrading.controllers.rest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.dto.portfolio.PortfolioDto;
import options.papertrading.facade.OptionFacade;
import options.papertrading.facade.PortfolioFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rest/portfolio")
@RequiredArgsConstructor
public class PortfolioRestController {
    private final PortfolioFacade portfolioFacade;
    private final OptionFacade optionFacade;

    @GetMapping
    public ResponseEntity<List<PortfolioDto>> showAllPortfolios() {
        List<PortfolioDto> portfoliosDto = portfolioFacade.showAllPortfolios();
        log.info("Request to show all portfolios. Result: {}", portfoliosDto);
        return portfoliosDto.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(portfoliosDto);
    }

    @GetMapping("/options")
    public ResponseEntity<List<OptionDto>> showOptionsList() {
        List<OptionDto> optionsDto = optionFacade.showOptionsList();
        log.info("Request to show options list. Result: {}", optionsDto);
        return optionsDto.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(optionsDto);
    }

    @PostMapping("/options")
    @Transactional
    public ResponseEntity<String> addPortfolio(@RequestBody @NonNull OptionDto optionDto,
                                                            @NonNull BindingResult bindingResult) {
        log.info("trying to add optionDto {} to portfolio", optionDto);
        optionFacade.volumeValidate(optionDto, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            portfolioFacade.addPortfolio(optionDto);
            return ResponseEntity.ok("Option added to portfolio");
        } catch (HttpServerErrorException exception) {
            log.error("Server error adding option to portfolio: {}", exception.getMessage(), exception);
            return ResponseEntity.internalServerError().body("Server error adding option to portfolio");
        }
    }
}