package options.papertrading.models;

import java.util.UUID;

public class Option {
    private String id;
    private String strike;
    private String price;

    public Option(String strike, String price) {
        this.id = UUID.randomUUID().toString();
        this.strike = strike;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStrike() {
        return strike;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}