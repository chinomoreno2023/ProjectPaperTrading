package options.papertrading.facade.interfaces;

public interface IPortfolioFacadeHtmlVersion extends IPortfolioFacade {
    void addPortfolio(String id, int volume, int buyOrWrite);
}
