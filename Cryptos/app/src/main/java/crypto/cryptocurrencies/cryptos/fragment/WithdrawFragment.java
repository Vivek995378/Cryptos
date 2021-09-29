package crypto.cryptocurrencies.cryptos.fragment;

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
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.activity.home.HomeActivity;
import crypto.cryptocurrencies.cryptos.database.FirestoreConstant;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.util.AppPreference;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WithdrawFragment extends Fragment {

    private EditText edtCoins, edtAddress;
    private TextView tvCryptoCoin;
    private Button btWithdraw;
    private PopupMenu coinMenu;
    private String selectedCoin = "";
    private HomeActivity homeActivity;

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

    public WithdrawFragment() {
        // Required empty public constructor
    }

    public WithdrawFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);

        edtCoins = view.findViewById(R.id.editText);
        edtAddress = view.findViewById(R.id.editText2);
        tvCryptoCoin = view.findViewById(R.id.textView16);
        btWithdraw = view.findViewById(R.id.button);

        bannerAdView = view.findViewById(R.id.maxAdView2);
        mrecAdView = view.findViewById(R.id.maxAdView3);

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

        coinMenu = new PopupMenu(getActivity(), tvCryptoCoin);

        coinMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectedCoin = item.getTitle().toString();
                tvCryptoCoin.setText(selectedCoin);
                return false;
            }
        });

        tvCryptoCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    coinMenu.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isInternetAvailable(getActivity())) {
                    User user = AppPreference.getUserDetails(getActivity());
                    if (edtCoins.getText().toString().isEmpty()) {
                        Toast.makeText(homeActivity, "Please enter coins", Toast.LENGTH_SHORT).show();
                    } else if (edtAddress.getText().toString().isEmpty()) {
                        Toast.makeText(homeActivity, "Please enter wallet address", Toast.LENGTH_SHORT).show();
                    } else {
                        if (user.getCoins() < 50000) {
                            Toast.makeText(homeActivity, "Minimum withdrawal is 50000 coins", Toast.LENGTH_SHORT).show();
                        } else {
                            sendData();
                        }
                    }
                } else {
                    Toast.makeText(homeActivity, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setMenu();
        return view;
    }

    private void sendData() {
        int enteredCoins = Integer.parseInt(edtCoins.getText().toString());
        User user = AppPreference.getUserDetails(getActivity());
        user.setCoins(user.getCoins() - enteredCoins);
        AppPreference.setUserDetails(getActivity(), user);
        Map<String, Object> map = new HashMap<>();
        map.put("coinName", selectedCoin);
        map.put("coinValue", edtCoins.getText().toString());
        map.put("creationDate", (System.currentTimeMillis() / 1000));
        map.put("paidAmount", 0.00);
        map.put("status", "Pending");
        map.put("userId", user.getUserId().trim());
        map.put("walletAddress", edtAddress.getText().toString().trim());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FirestoreConstant.WITHDRAW_TABLE)
                .document()
                .set(map);
        firestore.collection(FirestoreConstant.USER_TABLE)
                .document(user.getUserId().trim())
                .update("coins", user.getCoins());
        Toast.makeText(homeActivity, "Withdraw complete, Please wait for approval", Toast.LENGTH_SHORT).show();
        edtCoins.setText("");
        edtAddress.setText("");
    }

    public void setMenu() {
        try {
            coinMenu.getMenu().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < homeActivity.coinCodeList.size(); i++) {
            coinMenu.getMenu().add(homeActivity.coinCodeList.get(i).getCoinCode());
            if (i == 0) {
                selectedCoin = homeActivity.coinCodeList.get(i).getCoinCode();
            }
        }
    }
}