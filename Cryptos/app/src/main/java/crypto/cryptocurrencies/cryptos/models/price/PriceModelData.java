package crypto.cryptocurrencies.cryptos.models.price;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class PriceModelData {

    private String coinName;

    private String coinCode;

    private String coinImage;

    private double coinPrice;

    private double percentage;

    private double date;

    private boolean isHigh;

    private Bitmap chartBitmap;

    private ArrayList<Double> openValues;

    private ArrayList<Double> closeValues;

    private ArrayList<String> dateValues;

    public ArrayList<String> getDateValues() {
        return dateValues;
    }

    public void setDateValues(ArrayList<String> dateValues) {
        this.dateValues = dateValues;
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public Bitmap getChartBitmap() {
        return chartBitmap;
    }

    public void setChartBitmap(Bitmap chartBitmap) {
        this.chartBitmap = chartBitmap;
    }

    public ArrayList<Double> getCloseValues() {
        return closeValues;
    }

    public void setCloseValues(ArrayList<Double> closeValues) {
        this.closeValues = closeValues;
    }

    public ArrayList<Double> getOpenValues() {
        return openValues;
    }

    public void setOpenValues(ArrayList<Double> openValues) {
        this.openValues = openValues;
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

    public double getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(double coinPrice) {
        this.coinPrice = coinPrice;
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
