package crypto.cryptocurrencies.cryptos.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.bumptech.glide.Glide;
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.activity.home.HomeActivity;
import crypto.cryptocurrencies.cryptos.util.AppUtil;

import static crypto.cryptocurrencies.cryptos.util.AppUtil.hideProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.isInternetAvailable;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.round;

public class ConverterFragment extends Fragment {

    private ImageView imgFirstBottomExchange, imgSecondBottomExchange, imgFirstChart, imgSecondChart;
    private TextView tvTopFirstExchange, tvTopSecondExchange,
            tvBottomFirstCoinName, tvBottomSecondCoinName, tvBottomFirstCoinValue,
            tvBottomSecondCoinValue, tvFirstPercentage, tvSecondPercentage;
    private EditText edtFirst, edtSecond;
    private Button btConvert;
    private HomeActivity homeActivity;
    private PopupMenu firstMenu;
    private PopupMenu secondMenu;
    private String firstSelected = "", secondSelected = "", firstSelectedImageUrl = "",
            secondSelectedImageUrl = "";
    private MaxAdView bannerAdView;
    private MaxAdView mrecAdView;

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
        imgFirstBottomExchange = view.findViewById(R.id.imageView5);
        imgSecondBottomExchange = view.findViewById(R.id.imageView7);
        imgFirstChart = view.findViewById(R.id.imageView6);
        imgSecondChart = view.findViewById(R.id.imageView8);

        tvTopFirstExchange = view.findViewById(R.id.textView18);
        tvTopSecondExchange = view.findViewById(R.id.textView19);
        tvBottomFirstCoinName = view.findViewById(R.id.textView22);
        tvBottomSecondCoinName = view.findViewById(R.id.textView26);
        tvBottomFirstCoinValue = view.findViewById(R.id.textView23);
        tvBottomSecondCoinValue = view.findViewById(R.id.textView27);
        tvFirstPercentage = view.findViewById(R.id.textView25);
        tvSecondPercentage = view.findViewById(R.id.textView29);

        edtFirst = view.findViewById(R.id.editText3);
        edtSecond = view.findViewById(R.id.editText4);

        btConvert = view.findViewById(R.id.button1);

        firstMenu = new PopupMenu(getActivity(), tvTopFirstExchange);
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

        firstMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                firstSelected = item.getTitle().toString();
                tvTopFirstExchange.setText(firstSelected);
//                tvEditFirst.setText(firstSelected);
                for (int i = 0; i < homeActivity.coinCodeList.size(); i++) {
                    if (firstSelected.equals(homeActivity.coinCodeList.get(i).getCoinCode())) {
                        firstSelectedImageUrl = homeActivity.coinCodeList.get(i).getCoinImage();
                        break;
                    }
                }
//                Glide.with(getActivity()).load(firstSelectedImageUrl)
//                        .into(imgFirstTopExchange);
                return false;
            }
        });

        secondMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                secondSelected = item.getTitle().toString();
                tvTopSecondExchange.setText(secondSelected);
//                tvEditSecond.setText(secondSelected);
                for (int i = 0; i < homeActivity.coinCodeList.size(); i++) {
                    if (secondSelected.equals(homeActivity.coinCodeList.get(i).getCoinCode())) {
                        secondSelectedImageUrl = homeActivity.coinCodeList.get(i).getCoinImage();
                        break;
                    }
                }
//                Glide.with(getActivity()).load(secondSelectedImageUrl)
//                        .into(imgSecondTopExchange);
                return false;
            }
        });

        tvTopFirstExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    firstMenu.getMenu().clear();
                    setFirstMenu();
                    firstMenu.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvTopSecondExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    secondMenu.getMenu().clear();
                    setSecondMenu();
                    secondMenu.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setFirstMenu();
        firstSelected = homeActivity.coinCodeList.get(0).getCoinCode();
        tvTopFirstExchange.setText(firstSelected);
//        tvEditFirst.setText(firstSelected);
        firstSelectedImageUrl = homeActivity.coinCodeList.get(0).getCoinImage();
//        Glide.with(getActivity()).load(firstSelectedImageUrl)
//                .into(imgFirstTopExchange);
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
            double firstCoinValue = 0;
            double secondCoinValue = 0;
            for (int i = 0; i < homeActivity.coinCodeList.size(); i++) {
                if (firstSelected.equals(homeActivity.coinCodeList.get(i).getCoinCode())) {
                    if (homeActivity.coinCodeList.get(i).isHigh()) {
                        tvFirstPercentage.setText("+");
                        tvFirstPercentage.setTextColor(getResources().getColor(R.color.greenColor));
                    } else {
                        tvFirstPercentage.setText("-");
                        tvFirstPercentage.setTextColor(Color.RED);
                    }
                    tvFirstPercentage.append(String.valueOf(round(homeActivity.coinCodeList.
                            get(i).getPercentage(), 2)).replace("-", ""));
                    imgFirstChart.setImageBitmap(homeActivity.coinCodeList.get(i).getGraphImage());
                    tvBottomFirstCoinValue.setText("$" + homeActivity.coinCodeList.get(i).getCoinPrice());
                    firstCoinValue = homeActivity.coinCodeList.get(i).getCoinPrice();
                }

                if (secondSelected.equals(homeActivity.coinCodeList.get(i).getCoinCode())) {
                    if (homeActivity.coinCodeList.get(i).isHigh()) {
                        tvSecondPercentage.setText("+");
                        tvSecondPercentage.setTextColor(getResources().getColor(R.color.greenColor));
                    } else {
                        tvSecondPercentage.setText("-");
                        tvSecondPercentage.setTextColor(Color.RED);
                    }
                    tvSecondPercentage.append(String.valueOf(round(homeActivity.coinCodeList.get(i)
                            .getPercentage(), 2)).replace("-", ""));
                    imgSecondChart.setImageBitmap(homeActivity.coinCodeList.get(i).getGraphImage());
                    tvBottomSecondCoinValue.setText("$" + homeActivity.coinCodeList.get(i).getCoinPrice());
                    secondCoinValue = homeActivity.coinCodeList.get(i).getCoinPrice();
                }
            }

            Glide.with(getActivity()).load(firstSelectedImageUrl)
                    .into(imgFirstBottomExchange);
            Glide.with(getActivity()).load(secondSelectedImageUrl)
                    .into(imgSecondBottomExchange);

            tvBottomSecondCoinName.setText(secondSelected);
            tvBottomFirstCoinName.setText(firstSelected);

            int value = Integer.parseInt(edtFirst.getText().toString());
            edtSecond.setText(String.valueOf(round(((firstCoinValue * value) / secondCoinValue), 2)));
        } catch (Exception e) {
            Toast.makeText(homeActivity, "Please enter a valid value", Toast.LENGTH_SHORT).show();
        }
        hideProgressDialog();
    }

    private void setFirstMenu() {
        for (int i = 0; i < homeActivity.coinCodeList.size(); i++) {
            firstMenu.getMenu().add(homeActivity.coinCodeList.get(i).getCoinCode());
        }
    }

    private void setSecondMenu() {
        for (int i = 0; i < homeActivity.coinCodeList.size(); i++) {
            secondMenu.getMenu().add(homeActivity.coinCodeList.get(i).getCoinCode());
            if (secondSelected.isEmpty()) {
                secondSelected = homeActivity.coinCodeList.get(i).getCoinCode();
                tvTopSecondExchange.setText(secondSelected);
//                tvEditSecond.setText(secondSelected);
                secondSelectedImageUrl = homeActivity.coinCodeList.get(i).getCoinImage();
//                Glide.with(getActivity()).load(secondSelectedImageUrl)
//                        .into(imgSecondTopExchange);
            }
        }
    }
}