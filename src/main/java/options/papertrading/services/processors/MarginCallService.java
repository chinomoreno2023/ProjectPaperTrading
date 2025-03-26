package options.papertrading.services.processors;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import options.papertrading.dto.option.OptionDto;
import options.papertrading.facade.interfaces.IJournalFacade;
import options.papertrading.facade.interfaces.IPersonFacadeHtmlVersion;
import options.papertrading.models.person.Person;
import options.papertrading.models.portfolio.Portfolio;
import options.papertrading.repositories.PortfoliosRepository;
import options.papertrading.services.processors.interfaces.IMarginCallService;
import options.papertrading.services.processors.interfaces.IPortfolioManager;
import options.papertrading.util.converters.PortfolioConverter;
import options.papertrading.util.converters.TextConverter;
import options.papertrading.util.mail.SmtpMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarginCallService implements IMarginCallService {
    private final IPersonFacadeHtmlVersion personsService;
    private final PortfoliosRepository portfoliosRepository;
    private final PortfolioConverter portfolioConverter;
    private final IJournalFacade journalService;
    private final SmtpMailSender smtpMailSender;
    private final TextConverter textConverter;
    private final IPortfolioManager portfolioManager;

    @Value("${pathToMarginCallMail}")
    private String pathToMarginCallMail;

    @Transactional
    @Override
    public void marginCallCheck() {
        Person owner = personsService.getCurrentPerson();
        List<Portfolio> optionsInPortfolio = portfoliosRepository.findAllByOwner(owner);
        log.info("Owner: {}, options in portfolio: {}", owner, optionsInPortfolio);

        if (owner.getOpenLimit() < owner.getCurrentNetPosition() * 0.5) {
            int counterForJournals = 0;
            while (owner.getOpenLimit() <= owner.getCurrentNetPosition() * 0.5) {
                Portfolio portfolioWithMinVariat = optionsInPortfolio
                        .parallelStream()
                        .filter(portfolio -> portfolio.getVolume() != 0)
                        .min(Comparator.comparingDouble(Portfolio::getVariatMargin))
                        .orElse(null);
                log.info("Portfolio with min variat margin: {}", portfolioWithMinVariat);
                if (portfolioWithMinVariat == null) {
                    sendMarginCallMail(owner);
                    return;
                }

                OptionDto optionDto = portfolioConverter.convertPortfolioToOptionDto(portfolioWithMinVariat);
                owner.setOpenLimit(owner.getOpenLimit() + portfolioWithMinVariat.getVariatMargin());
                portfolioWithMinVariat.setVariatMargin(0);

                if (portfolioWithMinVariat.getVolume() >= 1) {
                    portfolioWithMinVariat.setVolume(portfolioWithMinVariat.getVolume() - 1);
                    optionDto.setBuyOrWrite(-1);
                }
                if (portfolioWithMinVariat.getVolume() <= -1) {
                    portfolioWithMinVariat.setVolume(portfolioWithMinVariat.getVolume() + 1);
                    optionDto.setBuyOrWrite(1);
                }

                counterForJournals++;
                optionDto.setVolume(1);
                journalService.addJournal(portfolioWithMinVariat, optionDto);
                owner.setOptionsInPortfolio(optionsInPortfolio);
                personsService.save(owner);
                portfolioManager.updateCurrentNetPositionAndOpenLimit();
                portfolioManager.updateAllVariatMargin();
            }

            if (counterForJournals > 0) {
                sendMarginCallMail(owner);
            }
        }
    }

    private void sendMarginCallMail(Person owner) {
        String message;
        try {
            message = String.format(textConverter.readMailTextFromFile(pathToMarginCallMail), owner.getUsername());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        try {
            smtpMailSender.sendHtml(owner.getEmail(), "Margin call", message);
        } catch (MessagingException exception) {
            throw new RuntimeException("Ошибка при отправке почты", exception);
        }
    }
}
