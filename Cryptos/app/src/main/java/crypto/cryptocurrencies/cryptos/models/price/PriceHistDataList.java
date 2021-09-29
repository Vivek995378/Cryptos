package crypto.cryptocurrencies.cryptos.models.price;

import java.util.ArrayList;

public class PriceHistDataList {

    private ArrayList<PriceHistData> Data;

    public ArrayList<PriceHistData> getData() {
        return Data;
    }

    public void setData(ArrayList<PriceHistData> Data) {
        this.Data = Data;
    }

}
