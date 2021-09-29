package bitcoin.collector.collection.fragment;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import bitcoin.collector.collection.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import bitcoin.collector.collection.activity.home.HomeActivity;
import bitcoin.collector.collection.retrofit.ApiInterface;
import bitcoin.collector.collection.retrofit.RetrofitClient;
import bitcoin.collector.collection.util.AppUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static bitcoin.collector.collection.util.AppUtil.hideProgressDialog;
import static bitcoin.collector.collection.util.AppUtil.isInternetAvailable;

public class ConverterFragment extends Fragment {

    private TextView tvTopSecondExchange, tvEditSecondValue;
    private EditText edtFirst;
    private Button btConvert;
    private HomeActivity homeActivity;
    private PopupMenu secondMenu;
    private String firstSelected = "BTC", secondSelected = "";
    private MaxAdView bannerAdView;
    private MaxAdView mrecAdView;
    private ArrayList<String> coinList;

    private MaxAdViewAdListener bannerAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdLoaded(MaxAd ad) {
            bannerAdView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError errorCode) {
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
        public void onAdDisplayFailed(MaxAd ad, MaxError errorCode) {

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
        public void onAdLoadFailed(String adUnitId, MaxError errorCode) {
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
        public void onAdDisplayFailed(MaxAd ad, MaxError errorCode) {

        }
    };

    public ConverterFragment() {
        // Required empty public constructor
    }

    public ConverterFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        tvTopSecondExchange = view.findViewById(R.id.textView19);
        tvEditSecondValue = view.findViewById(R.id.textView50);

        edtFirst = view.findViewById(R.id.editText3);

        btConvert = view.findViewById(R.id.button1);

        secondMenu = new PopupMenu(getActivity(), tvTopSecondExchange);
        bannerAdView = view.findViewById(R.id.maxAdView4);
        mrecAdView = view.findViewById(R.id.maxAdView7);

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

        coinList = new ArrayList<>();
        coinList.add("BTC");
        coinList.add("BCH");
        coinList.add("DOGE");
        coinList.add("ETH");
        coinList.add("BNB");
        coinList.add("XRP");
        coinList.add("ADA");
        coinList.add("LTC");
        coinList.add("DOT");
        coinList.add("LINK");
        coinList.add("USDT");
        coinList.add("TRX");
        coinList.add("ICP");

        secondMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                secondSelected = item.getTitle().toString();
                tvTopSecondExchange.setText(secondSelected);
                comparePrice();
                return false;
            }
        });

        tvTopSecondExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    secondMenu.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setSecondMenu();
        comparePrice();

        btConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable(getActivity())) {
                    if (!edtFirst.getText().toString().isEmpty()) {
                        comparePrice();
                    } else {
                        Toast.makeText(homeActivity, "Please enter conversion value", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(homeActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @SuppressLint("NewApi")
    private void comparePrice() {
        AppUtil.showProgressDialog(getActivity());
        try {
            ApiInterface apiInterface = RetrofitClient.getCompareCurrencyRetrofitInstance().create(ApiInterface.class);
            apiInterface.compareCrypto(firstSelected, secondSelected)
                    .enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            hideProgressDialog();
                            try {
                                if (response.isSuccessful()) {
                                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                                    double value = jsonObject.getJSONObject(firstSelected).getDouble(secondSelected) * Integer.valueOf(edtFirst.getText().toString());
                                    DecimalFormat REAL_FORMATTER = new DecimalFormat("0.########");
                                    tvEditSecondValue.setText(REAL_FORMATTER.format(value));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            hideProgressDialog();
                            Toast.makeText(homeActivity, "Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(homeActivity, "Please enter a valid value", Toast.LENGTH_SHORT).show();
        }
        hideProgressDialog();
    }

    private void setSecondMenu() {
        for (int i = 0; i < coinList.size(); i++) {
            secondMenu.getMenu().add(coinList.get(i));
            if (secondSelected.isEmpty()) {
                secondSelected = coinList.get(i);
                tvTopSecondExchange.setText(secondSelected);
            }
        }
    }
}