package crypto.cryptocurrencies.cryptos.fragment;

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
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.adapter.RewardsAdapter;
import crypto.cryptocurrencies.cryptos.listener.RewardAdClickListener;
import crypto.cryptocurrencies.cryptos.models.reward.RewardData;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.util.AppPreference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static crypto.cryptocurrencies.cryptos.database.FirestoreConstant.USER_TABLE;
import static crypto.cryptocurrencies.cryptos.util.AppPreference.setUserDetails;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.hideProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.prettyCount;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.showProgressDialog;
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

    private RewardAdClickListener listener = new RewardAdClickListener() {
        @Override
        public void onRewardAdClick(int pos) {
            showProgressDialog(getActivity());
            selectedPos = pos;
            switch (pos) {
                case 0:
                    loadInterstitialAd("c3afd743e795ea66");
                    break;
                case 1:
                    loadInterstitialAd("6890b6ed6f801eba");
                    break;
                case 2:
                    loadInterstitialAd("d8309b2bef5da962");
                    break;
                case 3:
                    type = "100";
                    setMaxRewardedAd("822b3d981a12c7e2");
                    break;
                case 4:
                    type = "200first";
                    setMaxRewardedAd("6f354173725ce754");
                    break;
                case 5:
                    loadInterstitialAd("362dbbd2339ec3a5");
                    break;
                case 6:
                    loadInterstitialAd("0f291a117f7161b7");
                    break;
                case 7:
                    type = "300first";
                    setMaxRewardedAd("291694ade5db74a7");
                    break;
                case 8:
                    type = "400first";
                    setMaxRewardedAd("8ab386fe4250ed0a");
                    break;
                case 9:
                    type = "500first";
                    setMaxRewardedAd("5825faf4e3492922");
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
        public void onAdLoadFailed(String adUnitId, MaxError error) {
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
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
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
        public void onAdLoadFailed(String adUnitId, MaxError error) {
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
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
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
        tvCoins.setText(prettyCount(mUser.getCoins()));
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
        int coin = mUser.getCoins();
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
                setMaxRewardedAd("5aeff5cb4e4d670a");
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
                setMaxRewardedAd("57d059d4d5259b6f");
                break;
            case "300second":
                type = "300third";
                setMaxRewardedAd("49347e6d3db4669e");
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
                setMaxRewardedAd("7d5a4f8edfd5ac1e");
                break;
            case "400second":
                type = "400third";
                setMaxRewardedAd("325ba522d87a43ff");
                break;
            case "400third":
                type = "400forth";
                setMaxRewardedAd("f3205674341808e3");
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
                setMaxRewardedAd("e90b7a0d972480a5");
                break;
            case "500second":
                type = "500third";
                setMaxRewardedAd("aa3d538b6debfd55");
                break;
            case "500third":
                type = "500forth";
                setMaxRewardedAd("2eab98fb2fa0486b");
                break;
            case "500forth":
                type = "500fifth";
                setMaxRewardedAd("053deda83c34dfee");
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
        setUserDetails(getActivity(), mUser);
        firestore.collection(USER_TABLE)
                .document(mUser.getUserId())
                .update("coins", mUser.getCoins());
        tvCoins.setText(String.valueOf(mUser.getCoins()));
    }
}