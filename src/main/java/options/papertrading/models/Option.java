package options.papertrading.models;

public class Option {
    private final String strike;

    public Option(String strike) {
        this.strike = strike;
    }

    public String getStrike() {
        return strike;
    }

}