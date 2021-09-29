package crypto.cryptocurrencies.cryptos.models.price;

import android.graphics.Bitmap;

public class PriceData {

    private String coinName;

    private String coinCode;

    private String coinImage;

    private double price;

    private double percentage;

    private boolean isHigh;

    private Bitmap chartBitmap;

    public PriceData() {
    }

    public Bitmap getChartBitmap() {
        return chartBitmap;
    }

    public void setChartBitmap(Bitmap chartBitmap) {
        this.chartBitmap = chartBitmap;
    }

    public String getCoinImage() {
        return coinImage;
    }

    public void setCoinImage(String coinImage) {
        this.coinImage = coinImage;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getCoinCode() {
        return coinCode;
    }

    public void setCoinCode(String coinCode) {
        this.coinCode = coinCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public boolean isHigh() {
        return isHigh;
    }

    public void setHigh(boolean high) {
        isHigh = high;
    }
}
