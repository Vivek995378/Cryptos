package bitcoin.collector.collection.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import bitcoin.collector.collection.R;
import bitcoin.collector.collection.adapter.RewardsAdapter;
import bitcoin.collector.collection.listener.RewardAdClickListener;
import bitcoin.collector.collection.models.reward.RewardData;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.util.AppPreference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static bitcoin.collector.collection.database.FirestoreConstant.USER_TABLE;
import static bitcoin.collector.collection.util.AppUtil.formatValue;
import static bitcoin.collector.collection.util.AppUtil.hideProgressDialog;
import static bitcoin.collector.collection.util.AppUtil.showProgressDialog;
import static com.github.mikephil.charting.utils.Utils.formatNumber;

public class RewardsFragment extends Fragment {

    public TextView tvCoins;
    private RecyclerView recyclerView;
    private String type = "100";
    private ArrayList<RewardData> arrayList;
    private RewardsAdapter adapter;
    private User mUser;
    private MaxAdView bannerAdView;
    private int selectedPos = 0;
    private MaxInterstitialAd interstitialAd;
    private MaxRewardedAd maxRewardedAd;
    private FirebaseFirestore firestore;

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

    private RewardAdClickListener listener = new RewardAdClickListener() {
        @Override
        public void onRewardAdClick(int pos) {
            showProgressDialog(getActivity());
            selectedPos = pos;
            switch (pos) {
                case 0:
                    loadInterstitialAd("c39f8d4802471c35");
                    break;
                case 1:
                    loadInterstitialAd("a855ebd7ae607aff");
                    break;
                case 2:
                    loadInterstitialAd("3d4b97f4e7a203b0");
                    break;
                case 3:
                    type = "100";
                    setMaxRewardedAd("4964c1d509854131");
                    break;
                case 4:
                    type = "200first";
                    setMaxRewardedAd("39f19992cd950cb1");
                    break;
                case 5:
                    loadInterstitialAd("fc5f0b5d2005c50b");
                    break;
                case 6:
                    loadInterstitialAd("f1ba517d5401e6c7");
                    break;
                case 7:
                    type = "300first";
                    setMaxRewardedAd("94301cfcdcb96f4f");
                    break;
                case 8:
                    type = "400first";
                    setMaxRewardedAd("2c8a54d3be256f9c");
                    break;
                case 9:
                    type = "500first";
                    setMaxRewardedAd("d5f66389833fa46d");
                    break;
            }
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
        public void onAdLoadFailed(String adUnitId, MaxError errorCode) {
            try {
                hideProgressDialog();
                Toast.makeText(getActivity(), "No rewards available", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {

        }

        @Override
        public void onAdHidden(MaxAd ad) {
            try {
                callMethodAfterCompleteAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdClicked(MaxAd ad) {

        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError errorCode) {
            try {
                hideProgressDialog();
                Toast.makeText(getActivity(), "No rewards available", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private MaxAdViewAdListener interstitialAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdExpanded(MaxAd ad) {

        }

        @Override
        public void onAdCollapsed(MaxAd ad) {

        }

        @Override
        public void onAdLoaded(MaxAd ad) {
            hideProgressDialog();
            try {
                interstitialAd.showAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError errorCode) {
            try {
                hideProgressDialog();
                Toast.makeText(getActivity(), "No rewards available", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            hideProgressDialog();
        }

        @Override
        public void onAdHidden(MaxAd ad) {
            hideProgressDialog();
            mUser.setCoins(mUser.getCoins() + 50);
            Toast.makeText(getActivity(), "You have earned 50 points.", Toast.LENGTH_SHORT).show();
            arrayList.get(selectedPos).setEarned(true);
            adapter.notifyItemChanged(selectedPos);
            updateBalance();
        }

        @Override
        public void onAdClicked(MaxAd ad) {

        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError errorCode) {
            hideProgressDialog();
            Toast.makeText(getActivity(), "No rewards available", Toast.LENGTH_SHORT).show();
        }
    };

    public RewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        mUser = AppPreference.getUserDetails(getActivity());

        tvCoins = view.findViewById(R.id.textView30);
        recyclerView = view.findViewById(R.id.recyclerView1);
        bannerAdView = view.findViewById(R.id.maxAdView6);
        try {
            bannerAdView.setListener(bannerAdListener);
            bannerAdView.loadAd();
            bannerAdView.startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        firestore = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        setData();

        return view;
    }

    private void setData() {
        tvCoins.setText(formatValue(mUser.getCoins()));
        arrayList = new ArrayList<>();
        arrayList.add(new RewardData(50, false, 1));
        arrayList.add(new RewardData(50, false, 1));
        arrayList.add(new RewardData(50, false, 1));
        arrayList.add(new RewardData(100, false, 1));
        arrayList.add(new RewardData(200, false, 2));
        arrayList.add(new RewardData(50, false, 1));
        arrayList.add(new RewardData(50, false, 1));
        arrayList.add(new RewardData(300, false, 3));
        arrayList.add(new RewardData(400, false, 4));
        arrayList.add(new RewardData(500, false, 5));
        adapter = new RewardsAdapter(getActivity(), arrayList, listener);
        recyclerView.setAdapter(adapter);
    }

    private void loadInterstitialAd(String adId) {
        showProgressDialog(getActivity());
        interstitialAd = new MaxInterstitialAd(adId, getActivity());
        interstitialAd.setListener(interstitialAdListener);
        interstitialAd.loadAd();
    }

    private void setMaxRewardedAd(String adId) {
        showProgressDialog(getActivity());
        maxRewardedAd = MaxRewardedAd.getInstance(adId, getActivity());
        maxRewardedAd.setListener(rewardedAdListener);
        maxRewardedAd.loadAd();
    }

    private void callMethodAfterCompleteAd() {
        float coin = mUser.getCoins();
        hideProgressDialog();
        switch (type) {
            case "50":
                mUser.setCoins(coin + 50);
                Toast.makeText(getActivity(), "You have earned 50 points.", Toast.LENGTH_SHORT).show();
                arrayList.get(selectedPos).setEarned(true);
                adapter.notifyItemChanged(selectedPos);
                updateBalance();
                break;
            case "100":
                mUser.setCoins(coin + 100);
                Toast.makeText(getActivity(), "You have earned 100 points.", Toast.LENGTH_SHORT).show();
                arrayList.get(selectedPos).setEarned(true);
                adapter.notifyItemChanged(selectedPos);
                updateBalance();
                break;
            case "200first":
                type = "200second";
                setMaxRewardedAd("42050ff1fe454d40");
                break;
            case "200second":
                mUser.setCoins(coin + 200);
                Toast.makeText(getActivity(), "You have earned 200 points.", Toast.LENGTH_SHORT).show();
                arrayList.get(selectedPos).setEarned(true);
                adapter.notifyItemChanged(selectedPos);
                updateBalance();
                break;
            case "300first":
                type = "300second";
                setMaxRewardedAd("ac683120bf2dc864");
                break;
            case "300second":
                type = "300third";
                setMaxRewardedAd("4efb79ba48f77571");
                break;
            case "300third":
                mUser.setCoins(coin + 300);
                Toast.makeText(getActivity(), "You have earned 300 points.", Toast.LENGTH_SHORT).show();
                arrayList.get(selectedPos).setEarned(true);
                adapter.notifyItemChanged(selectedPos);
                updateBalance();
                break;
            case "400first":
                type = "400second";
                setMaxRewardedAd("bdaf77b8119dae90");
                break;
            case "400second":
                type = "400third";
                setMaxRewardedAd("c03ff9ac60ed585f");
                break;
            case "400third":
                type = "400forth";
                setMaxRewardedAd("cf1c1a39ebd4bbb9");
                break;
            case "400forth":
                mUser.setCoins(coin + 400);
                Toast.makeText(getActivity(), "You have earned 400 points.", Toast.LENGTH_SHORT).show();
                arrayList.get(selectedPos).setEarned(true);
                adapter.notifyItemChanged(selectedPos);
                updateBalance();
                break;
            case "500first":
                type = "500second";
                setMaxRewardedAd("a4c3d4d0a991f132");
                break;
            case "500second":
                type = "500third";
                setMaxRewardedAd("61d6bc70fd478c02");
                break;
            case "500third":
                type = "500forth";
                setMaxRewardedAd("6ee20a4ded4869c6");
                break;
            case "500forth":
                type = "500fifth";
                setMaxRewardedAd("c546749dcf5adfac");
                break;
            case "500fifth":
                Toast.makeText(getActivity(), "You have earned 500 points.", Toast.LENGTH_SHORT).show();
                mUser.setCoins(coin + 500);
                arrayList.get(selectedPos).setEarned(true);
                adapter.notifyItemChanged(selectedPos);
                updateBalance();
                break;
        }
    }

    private void updateBalance() {
        AppPreference.setUserDetails(getActivity(), mUser);
        firestore.collection(USER_TABLE)
                .document(mUser.getUserId())
                .update("coins", mUser.getCoins());
        tvCoins.setText(formatValue(mUser.getCoins()));
    }
}