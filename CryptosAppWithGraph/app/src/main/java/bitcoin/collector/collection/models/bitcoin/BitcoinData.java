package bitcoin.collector.collection.models.bitcoin;

import java.io.Serializable;

public class BitcoinData implements Serializable {

    private String countryName;
    private String price;
    private String countryFlag;

    public BitcoinData(String countryName, String price, String countryFlag) {
        this.countryName = countryName;
        this.price = price;
        this.countryFlag = countryFlag;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }
}
