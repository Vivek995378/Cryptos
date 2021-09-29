package bitcoin.collector.collection.models.home;

import android.graphics.Bitmap;

public class HomeCoinCode {

    private String coinCode;

    private String coinImage;

    private double percentage;

    private Bitmap graphImage;

    private boolean isHigh;

    private double coinPrice;

    public HomeCoinCode() {
    }

    public HomeCoinCode(String coinCode, String coinImage) {
        this.coinCode = coinCode;
        this.coinImage = coinImage;
    }

    public double getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(double coinPrice) {
        this.coinPrice = coinPrice;
    }

    public boolean isHigh() {
        return isHigh;
    }

    public void setHigh(boolean high) {
        isHigh = high;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Bitmap getGraphImage() {
        return graphImage;
    }

    public void setGraphImage(Bitmap graphImage) {
        this.graphImage = graphImage;
    }

    public String getCoinCode() {
        return coinCode;
    }

    public void setCoinCode(String coinCode) {
        this.coinCode = coinCode;
    }

    public String getCoinImage() {
        return coinImage;
    }

    public void setCoinImage(String coinImage) {
        this.coinImage = coinImage;
    }
}
