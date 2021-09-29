package crypto.cryptocurrencies.cryptos.activity.coinDetail;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.bumptech.glide.Glide;
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.models.price.PriceHistPojo;
import crypto.cryptocurrencies.cryptos.models.price.PriceModelData;
import crypto.cryptocurrencies.cryptos.retrofit.ApiInterface;
import crypto.cryptocurrencies.cryptos.retrofit.RetrofitClient;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static crypto.cryptocurrencies.cryptos.util.AppUtil.hideProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.round;

public class CoinDetailsActivity extends AppCompatActivity {

    private LineChart lineChart;
    private String coinCode, coinName, coinImage, selectedValue = "Last Week";
    private TextView tvCoinCode, tvCoinName, tvDropDown, tvPrice, tvPercentage, tvHigh, tvLow;
    private ImageView imgCoin, imgHighAndLow;
    private PopupMenu popupMenu;
    private LinearLayout llHighAndLow;
    private double lowPrice = 0, highPrice = 0, closePrice = 0, openPrice = 0;
    private MaxAdView bannerAdView;
    private MaxAdView mrecAdView;
    private MaxRewardedAd maxRewardedAd;

    private MaxAdViewAdListener bannerAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdLoaded(MaxAd ad) {
            bannerAdView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            try {
                bannerAdView.loadAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {

        }

        @Override
        public void onAdHidden(MaxAd ad) {

        }

        @Override
        public void onAdClicked(MaxAd ad) {

        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {

        }

        @Override
        public void onAdExpanded(MaxAd ad) {

        }

        @Override
        public void onAdCollapsed(MaxAd ad) {

        }
    };

    private MaxAdViewAdListener mrecAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdExpanded(MaxAd ad) {

        }

        @Override
        public void onAdCollapsed(MaxAd ad) {

        }

        @Override
        public void onAdLoaded(MaxAd ad) {
            try {
                mrecAdView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            try {
                mrecAdView.loadAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {

        }

        @Override
        public void onAdHidden(MaxAd ad) {

        }

        @Override
        public void onAdClicked(MaxAd ad) {

        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {

        }
    };

    private MaxRewardedAdListener rewardedAdListener = new MaxRewardedAdListener() {
        @Override
        public void onRewardedVideoStarted(MaxAd ad) {

        }

        @Override
        public void onRewardedVideoCompleted(MaxAd ad) {

        }

        @Override
        public void onUserRewarded(MaxAd ad, MaxReward reward) {

        }

        @Override
        public void onAdLoaded(MaxAd ad) {
            try {
                maxRewardedAd.showAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            try {
                hideProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {

        }

        @Override
        public void onAdHidden(MaxAd ad) {
        }

        @Override
        public void onAdClicked(MaxAd ad) {

        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            try {
                hideProgressDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void loadAd() {
        maxRewardedAd = MaxRewardedAd.getInstance("ee0c0488904877dc", this);
        maxRewardedAd.setListener(rewardedAdListener);
        maxRewardedAd.loadAd();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_details);
        AppUtil.setStatusBarGradiant(this);

        lineChart = findViewById(R.id.lineChart1);
        imgCoin = findViewById(R.id.imageView1);
        tvCoinCode = findViewById(R.id.textView9);
        tvCoinName = findViewById(R.id.textView10);
        tvDropDown = findViewById(R.id.textView13);
        llHighAndLow = findViewById(R.id.linearLayout5);
        tvPrice = findViewById(R.id.textView11);
        tvPercentage = findViewById(R.id.textView12);
        imgHighAndLow = findViewById(R.id.imageView2);
        tvHigh = findViewById(R.id.textView14);
        tvLow = findViewById(R.id.textView15);
        bannerAdView = findViewById(R.id.maxAdView1);
        mrecAdView = findViewById(R.id.maxAdView8);

        if(getIntent().getIntExtra("adNumber", 0) == 5){
            loadAd();
        }

        try {
            bannerAdView.setListener(bannerAdListener);
            bannerAdView.loadAd();
            bannerAdView.startAutoRefresh();
            mrecAdView.setListener(mrecAdListener);
            mrecAdView.loadAd();
            mrecAdView.startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        coinCode = getIntent().getStringExtra("coinCode");
        coinName = getIntent().getStringExtra("coinName");
        coinImage = getIntent().getStringExtra("coinImage");

        tvCoinName.setText(coinName);
        tvCoinCode.setText(coinCode);
        Glide.with(this).load(coinImage).into(imgCoin);

        setDropdown();
        AppUtil.showProgressDialog(this);
        getData("6");
    }

    private void setDropdown() {
        popupMenu = new PopupMenu(this, tvDropDown);
        popupMenu.getMenu().add("Today");
        popupMenu.getMenu().add("Last Day");
        popupMenu.getMenu().add("Last Week");
        popupMenu.getMenu().add("Last Month");
        popupMenu.getMenu().add("Last Year");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectedValue = item.getTitle().toString();
                tvDropDown.setText(selectedValue);
                AppUtil.showProgressDialog(CoinDetailsActivity.this);
                switch (selectedValue) {
                    case "Today":
                        getData("1");
                        break;
                    case "Last Day":
                        getData("1");
                        break;
                    case "Last Week":
                        getData("6");
                        break;
                    case "Last Month":
                        getData("28");
                        break;
                    case "Last Year":
                        getData("360");
                        break;
                }
                return false;
            }
        });

        tvDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
    }

    private void getData(String limit) {
        highPrice = 0;
        lowPrice = 0;
        ApiInterface apiInterface = RetrofitClient.getAllCurrencyRetrofitInstance().create(ApiInterface.class);
        apiInterface.getCurrencyPriceList(coinCode, "USD", limit)
                .enqueue(new Callback<PriceHistPojo>() {
                    @Override
                    public void onResponse(Call<PriceHistPojo> call, Response<PriceHistPojo> response) {
                        AppUtil.hideProgressDialog();
                        try {
                            PriceModelData model = new PriceModelData();
                            llHighAndLow.setVisibility(View.VISIBLE);
                            tvPrice.setText("$" + round(response.body().getData().getData()
                                    .get((response.body().getData().getData().size() - 1)).getClose(), 2));
                            ArrayList<Double> openValuas = new ArrayList<>();
                            ArrayList<Double> closeValuas = new ArrayList<>();
                            ArrayList<String> dateValuas = new ArrayList<>();
                            ArrayList<Entry> entryArrayList = new ArrayList<>();

                            if (selectedValue.equals("Today")) {
                                openValuas.add(response.body().getData().getData().get(1).getOpen());
                                closeValuas.add(response.body().getData().getData().get(1).getClose());
                                dateValuas.add(getDate((long) response.body().getData().getData().get(1).getTime(), "dd/MM"));
                                lowPrice = response.body().getData().getData().get(1).getLow();
                                highPrice = response.body().getData().getData().get(1).getHigh();
                                closePrice = response.body().getData().getData()
                                        .get(1).getClose();
                                openPrice = response.body().getData().getData()
                                        .get(1).getOpen();
                            } else {
                                closePrice = response.body().getData().getData()
                                        .get((response.body().getData().getData().size() - 1)).getClose();
                                openPrice = response.body().getData().getData()
                                        .get(0).getOpen();
                                for (int i = 0; i < response.body().getData().getData().size(); i++) {
                                    openValuas.add(response.body().getData().getData().get(i).getOpen());
                                    closeValuas.add(response.body().getData().getData().get(i).getClose());
                                    dateValuas.add(getDate((long) response.body().getData().getData().get(i).getTime(), "dd/MM"));
                                    if (response.body().getData().getData().get(i).getHigh() > highPrice) {
                                        highPrice = response.body().getData().getData().get(i).getHigh();
                                    }
                                    if (lowPrice == 0) {
                                        lowPrice = response.body().getData().getData().get(i).getLow();
                                    } else if (response.body().getData().getData().get(i).getLow() < lowPrice) {
                                        lowPrice = response.body().getData().getData().get(i).getLow();
                                    }
                                }
                            }
                            model.setCoinPrice(closePrice);
                            double percentage = (openPrice - closePrice) / closePrice * 100;
                            tvPercentage.getText().toString().replace("-", "");
                            if (closePrice > openPrice) {
                                String text = String.valueOf(round(percentage, 2));
                                tvPercentage.setText("+" + text.replace("-", ""));
                                tvPercentage.setTextColor(getResources().getColor(R.color.greenColor));
                                imgHighAndLow.setBackgroundResource(R.drawable.ic_arrow_up);
                            } else {
                                tvPercentage.setText("-" + String.valueOf(round(percentage, 2)).replace("-", ""));
                                tvPercentage.setTextColor(Color.RED);
                                imgHighAndLow.setBackgroundResource(R.drawable.ic_arrow_down);
                            }
                            model.setPercentage(round(percentage, 2));
                            model.setOpenValues(openValuas);
                            model.setCloseValues(closeValuas);
                            model.setDateValues(dateValuas);
                            tvLow.setText(String.valueOf(round(lowPrice, 2)));
                            tvHigh.setText(String.valueOf(round(highPrice, 2)));
                            for (int i = 0; i < model.getOpenValues().size(); i++) {
                                entryArrayList.add(new Entry(i, model.getCloseValues().get(i).floatValue()));
                            }
                            setLineChart(model, entryArrayList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<PriceHistPojo> call, Throwable t) {
                        hideProgressDialog();
                        Toast.makeText(CoinDetailsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLineChart(final PriceModelData model, ArrayList<Entry> entryList) {
        lineChart.removeAllViews();
        lineChart.clear();
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        lineDataSet.setColor(getResources().getColor(R.color.colorPrimary));
        lineDataSet.setLineWidth(1f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setGridColor(getResources().getColor(R.color.textColor));
        lineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.textColor));
        lineChart.getLegend().setEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setPinchZoom(false);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setFillDrawable(getResources().getDrawable(R.drawable.graph_gradient));
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        if (selectedValue.equals("Today") || selectedValue.equals("Last Day") || selectedValue.equals("Last Week")) {
            xAxis.setLabelCount(model.getDateValues().size(), true);
        }
        xAxis.setTextSize(10f);
        xAxis.setTextColor(getResources().getColor(R.color.textColor));
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return model.getDateValues().get((int) value % model.getDateValues().size());
            }
        });

        LineData lineData = new LineData(iLineDataSets);
        lineData.setValueTextColor(Color.TRANSPARENT);
        lineChart.setData(lineData);
        lineChart.animateXY(500, 500);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    public static String getDate(long millis, String dateFormat) {
        millis = millis * 1000;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - millis);
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - millis);
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - millis);

        if (seconds < 60) {
            return String.valueOf(seconds) + "s";
        } else if (minutes == 1) {
            return String.valueOf(minutes) + "m";
        } else if (minutes < 60) {
            return String.valueOf(minutes) + "m";
        } else if (hours == 1) {
            return String.valueOf(hours) + "h";
        } else if (hours < 24) {
            return String.valueOf(hours) + "h";
        } else if (days == 1) {
            return String.valueOf(days) + "d";
        } else if (days < 30) {
            return String.valueOf(days) + "d";
        } else if (days > 360) {
            return (days / 360) + "y";
        } else if (days > 30) {
            return (days / 30) + "m";
        } else {
            return (days / 7) + "w";
        }
    }

    public void finishStatistics(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}