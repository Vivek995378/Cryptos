package crypto.cryptocurrencies.cryptos.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.bumptech.glide.Glide;
import com.cryptos.cryptocurrency.R;

import crypto.cryptocurrencies.cryptos.activity.aboutAndPrivacy.AboutAndPrivacyActivity;
import crypto.cryptocurrencies.cryptos.activity.editProfile.EditProfileActivity;
import crypto.cryptocurrencies.cryptos.activity.help.HelpActivity;
import crypto.cryptocurrencies.cryptos.activity.login.LoginActivity;
import crypto.cryptocurrencies.cryptos.activity.withdrawHistory.WithdrawHistoryActivity;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.util.AppPreference;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static crypto.cryptocurrencies.cryptos.database.FirestoreConstant.USER_TABLE;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.clearAllIntent;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.setDialogThemeButton;

public class ProfileFragment extends Fragment {

    private ImageView imgUser;
    private TextView tvName, tvEmailOrMobile, tvEditProfile, tvWithdrawHistory, tvAboutUs, tvPrivacyPolicy, tvHelp, tvLogout;
    private User mUser;
    private GoogleSignInClient signInClient;
    private String privacyUrl = "https://aboutuscryptocurrency.blogspot.com/2021/05/policy-we-use-information-we-collect-or.html";
    private String aboutUs = "https://aboutuscryptocurrency.blogspot.com/2021/05/us-we-are-digital-platform-that-helps.html";

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgUser = view.findViewById(R.id.imageView11);
        tvName = view.findViewById(R.id.textView33);
        tvEmailOrMobile = view.findViewById(R.id.textView34);
        tvWithdrawHistory = view.findViewById(R.id.textView35);
        tvAboutUs = view.findViewById(R.id.textView36);
        tvPrivacyPolicy = view.findViewById(R.id.textView37);
        tvHelp = view.findViewById(R.id.textView38);
        tvLogout = view.findViewById(R.id.textView39);
        bannerAdView = view.findViewById(R.id.maxAdView5);
        tvEditProfile = view.findViewById(R.id.textView50);

        try {
            bannerAdView.setListener(bannerAdListener);
            bannerAdView.loadAd();
            bannerAdView.startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openLogoutDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvWithdrawHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WithdrawHistoryActivity.class));
            }
        });

        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });

        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutAndPrivacyActivity.class);
                intent.putExtra("isAbout", true);
                startActivity(intent);
            }
        });

        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutAndPrivacyActivity.class);
                intent.putExtra("isAbout", false);
                startActivity(intent);
            }
        });

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        setUserDetails();
        return view;
    }

    public void setUserDetails() {
        mUser = AppPreference.getUserDetails(getActivity());
        if (mUser.getName() != null) {
            tvName.setText(mUser.getName());
        }

        if (mUser.getEmail() != null) {
            tvEmailOrMobile.setText(mUser.getEmail());
        } else if (mUser.getPhone() != null) {
            tvEmailOrMobile.setText(mUser.getPhone());
        }

        if (mUser.getProfilePic() != null) {
            Glide.with(getActivity()).load(mUser.getProfilePic())
                    .circleCrop()
                    .into(imgUser);
        }

    }

    private void openLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                updateBalanceInRef();
                AppPreference.clearAllPreferences(getActivity());
                try {
                    LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    signInClient = GoogleSignIn.getClient(getActivity(), gso);
                    signInClient.signOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                clearAllIntent(intent);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setDialogThemeButton(getActivity(), builder);
    }

    private void updateBalanceInRef() {
        Map<String, Object> map = new HashMap<>();
        map.put("fcmToken", "");
        map.put("coins", mUser.getCoins());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_TABLE)
                .document(mUser.getUserId())
                .update(map);
    }
}