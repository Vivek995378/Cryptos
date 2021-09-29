package bitcoin.collector.collection.fragment;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.activity.editProfile.EditProfileActivity;
import bitcoin.collector.collection.activity.login.LoginActivity;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.util.AppPreference;

import static bitcoin.collector.collection.database.FirestoreConstant.USER_TABLE;
import static bitcoin.collector.collection.util.AppUtil.clearAllIntent;
import static bitcoin.collector.collection.util.AppUtil.setDialogThemeButton;

public class ProfileFragment extends Fragment {

    private ImageView imgUser;
    private TextView tvName, tvEmailOrMobile, tvEditProfile, tvWithdrawHistory, tvAboutUs, tvPrivacyPolicy, tvHelp, tvLogout;
    private User mUser;
    private GoogleSignInClient signInClient;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgUser = view.findViewById(R.id.imageView11);
        tvName = view.findViewById(R.id.textView33);
        tvEmailOrMobile = view.findViewById(R.id.textView34);
        bannerAdView = view.findViewById(R.id.maxAdView5);
        tvEditProfile = view.findViewById(R.id.textView50);
        try {
            bannerAdView.setListener(bannerAdListener);
            bannerAdView.loadAd();
            bannerAdView.startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivityIfNeeded(intent, 1005);
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