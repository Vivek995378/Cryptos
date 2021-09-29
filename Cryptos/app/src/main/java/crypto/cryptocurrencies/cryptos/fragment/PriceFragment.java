package crypto.cryptocurrencies.cryptos.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.activity.home.HomeActivity;
import crypto.cryptocurrencies.cryptos.adapter.PriceAdapter;
import crypto.cryptocurrencies.cryptos.database.FirestoreConstant;
import crypto.cryptocurrencies.cryptos.models.home.HomeCoinCode;
import crypto.cryptocurrencies.cryptos.models.price.PriceData;
import crypto.cryptocurrencies.cryptos.models.price.PriceHistPojo;
import crypto.cryptocurrencies.cryptos.retrofit.ApiInterface;
import crypto.cryptocurrencies.cryptos.retrofit.RetrofitClient;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static crypto.cryptocurrencies.cryptos.util.AppUtil.round;


public class PriceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private EditText edtSearch;
    private RecyclerView rvPrice;
    private LineChart lineChart;
    private ArrayList<PriceData> priceList;
    private FirebaseFirestore firestore;
    private ApiInterface apiInterface;
    private PriceAdapter priceAdapter;
    private SwipeRefreshLayout refreshLayout;
    private HomeActivity homeActivity;
    private MaxAdView bannerAdView;

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

    public PriceFragment() {
        // Required empty public constructor
    }

    public PriceFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        edtSearch = view.findViewById(R.id.editText6);
        rvPrice = view.findViewById(R.id.recyclerView);
        lineChart = view.findViewById(R.id.lineChart);
        bannerAdView = view.findViewById(R.id.maxAdView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);

        try {
            bannerAdView.setListener(bannerAdListener);
            bannerAdView.loadAd();
            bannerAdView.startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        priceList = new ArrayList<>();

        priceAdapter = new PriceAdapter(getActivity(), priceList);
        rvPrice.setAdapter(priceAdapter);

        firestore = FirebaseFirestore.getInstance();
        apiInterface = RetrofitClient.getAllCurrencyRetrofitInstance().create(ApiInterface.class);

        if (!AppUtil.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else {
            getPriceList();
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().isEmpty()) {
                    priceAdapter.updateAdapter(priceList);
                } else {
                    filterCategoryList(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void getPriceList() {
        homeActivity.coinCodeList = new ArrayList<>();
        AppUtil.showProgressDialog(getActivity());
        firestore.collection(FirestoreConstant.CRYPTO_LIST_TABLE)
                .orderBy("position", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    PriceData data = snapshot.toObject(PriceData.class);
                                    if (data.getCoinName() != null && data.getCoinCode() != null &&
                                            data.getCoinImage() != null) {
                                        priceList.add(data);
                                        homeActivity.coinCodeList.add(new HomeCoinCode(data.getCoinCode(), data.getCoinImage()));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (priceList.size() > 0) {
                                getCoinData(0);
                            } else {
                                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void getCoinData(final int i) {
        String coinCode = priceList.get(i).getCoinCode();
        apiInterface.getCurrencyPriceList(coinCode, "USD", "10")
                .enqueue(new Callback<PriceHistPojo>() {
                    @Override
                    public void onResponse(Call<PriceHistPojo> call, Response<PriceHistPojo> response) {
                        try {
                            PriceData data = priceList.get(i);
                            double closePrice = response.body().getData().getData()
                                    .get((response.body().getData().getData().size() - 1)).getClose();
                            double openPrice = response.body().getData().getData()
                                    .get((response.body().getData().getData().size() - 1)).getOpen();
                            data.setPrice(round(closePrice, 2));
                            homeActivity.coinCodeList.get(i).setCoinPrice(data.getPrice());
                            if (closePrice > openPrice) {
                                data.setHigh(true);
                                homeActivity.coinCodeList.get(i).setHigh(true);
                            }
                            double percentage = (openPrice - closePrice) / closePrice * 100;
                            homeActivity.coinCodeList.get(i).setPercentage(percentage);
                            data.setPercentage(round(percentage, 2));
                            ArrayList<Double> openValuas = new ArrayList<>();
                            ArrayList<Double> closeValuas = new ArrayList<>();
                            ArrayList<Entry> entryArrayList = new ArrayList<>();
                            for (int i = 0; i < response.body().getData().getData().size(); i++) {
                                openValuas.add(response.body().getData().getData().get(i).getOpen());
                                closeValuas.add(response.body().getData().getData().get(i).getClose());
                            }
                            Collections.reverse(openValuas);
                            Collections.reverse(closeValuas);
                            for (int i = 0; i < closeValuas.size(); i++) {
                                entryArrayList.add(new Entry(i, closeValuas.get(i).floatValue()));
                            }
                            setLineChart(i, data, entryArrayList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (i == (priceList.size() - 1)) {
                                AppUtil.hideProgressDialog();
                                priceAdapter.notifyDataSetChanged();
                            } else {
                                int pos = i + 1;
                                getCoinData(pos);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PriceHistPojo> call, Throwable t) {
                        AppUtil.hideProgressDialog();
                        Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLineChart(int pos, PriceData model, ArrayList<Entry> entryList) {
        lineChart.removeAllViews();
        lineChart.clear();
        ArrayList<String> dateList = new ArrayList<>();
        for (int i = 0; i < entryList.size(); i++) {
            dateList.add("" + i);
        }
        LineDataSet lineDataSet = new LineDataSet(entryList, "");
        if (model.isHigh()) {
            lineDataSet.setColor(Color.GREEN);
        } else {
            lineDataSet.setColor(Color.RED);
        }
        lineDataSet.setLineWidth(3f);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        lineChart.getLegend().setEnabled(false);
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawCircles(false);
        if(model.isHigh()) {
            lineDataSet.setFillDrawable(getResources().getDrawable(R.drawable.graph_green_gradient));
        }else{
            lineDataSet.setFillDrawable(getResources().getDrawable(R.drawable.graph_red_gradient));
        }
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(lineDataSet);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateList));
        xAxis.setLabelRotationAngle(65f);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(8f);

        LineData lineData = new LineData(iLineDataSets);
        lineData.setValueTextColor(getResources().getColor(R.color.transparent));
        lineChart.setData(lineData);
        model.setChartBitmap(lineChart.getChartBitmap());
        homeActivity.coinCodeList.get(pos).setGraphImage(lineChart.getChartBitmap());
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void filterCategoryList(String value) {
        ArrayList<PriceData> tempList = new ArrayList<>();

        for (PriceData s : priceList) {
            if (s.getCoinName().toLowerCase().contains(value.toLowerCase()) ||
                    s.getCoinCode().toLowerCase().contains(value.toLowerCase())
                            && !tempList.toString().contains(s.getCoinName())) {
                tempList.add(s);
            }
        }

        HashSet<PriceData> hashSet = new HashSet<>();
        hashSet.addAll(tempList);
        tempList.clear();
        tempList.addAll(hashSet);
        priceAdapter.updateAdapter(tempList);
    }

    @Override
    public void onRefresh() {
        try {
            refreshLayout.setRefreshing(false);
            if (AppUtil.isInternetAvailable(getActivity())) {
                priceList.clear();
                priceAdapter.notifyDataSetChanged();
                getPriceList();
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}