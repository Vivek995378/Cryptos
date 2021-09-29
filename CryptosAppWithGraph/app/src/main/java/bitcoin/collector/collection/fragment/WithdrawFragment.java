package bitcoin.collector.collection.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import bitcoin.collector.collection.R;
import bitcoin.collector.collection.activity.home.HomeActivity;
import bitcoin.collector.collection.database.FirestoreConstant;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.util.AppPreference;
import bitcoin.collector.collection.util.AppUtil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WithdrawFragment extends Fragment {

    private EditText edtCoins, edtAddress;
    private Button btWithdraw;

    private MaxAdView bannerAdView;
    private MaxAdView mrecAdView;

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

    public WithdrawFragment() {
        // Required empty public constructor
    }

    public WithdrawFragment(HomeActivity homeActivity) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);

        edtCoins = view.findViewById(R.id.editText4);
        edtAddress = view.findViewById(R.id.editText2);
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

        btWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isInternetAvailable(getActivity())) {
                    User user = AppPreference.getUserDetails(getActivity());
                    if (edtCoins.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter coins", Toast.LENGTH_SHORT).show();
                    } else if (edtAddress.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter wallet address", Toast.LENGTH_SHORT).show();
                    } else {
                        if (user.getCoins() < 50000) {
                            Toast.makeText(getActivity(), "Minimum withdrawal is 50000 coins", Toast.LENGTH_SHORT).show();
                        } else {
                            sendData();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void sendData() {
        int enteredCoins = Integer.parseInt(edtCoins.getText().toString());
        User user = AppPreference.getUserDetails(getActivity());
        user.setCoins(user.getCoins() - enteredCoins);
        AppPreference.setUserDetails(getActivity(), user);
        Map<String, Object> map = new HashMap<>();
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
        Toast.makeText(getActivity(), "Withdraw complete, Please wait for approval", Toast.LENGTH_SHORT).show();
        edtCoins.setText("");
        edtAddress.setText("");
    }
}