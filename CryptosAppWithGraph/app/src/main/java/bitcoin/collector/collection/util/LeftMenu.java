package bitcoin.collector.collection.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.activity.aboutAndPrivacy.AboutAndPrivacyActivity;
import bitcoin.collector.collection.activity.help.HelpActivity;
import bitcoin.collector.collection.activity.login.LoginActivity;
import bitcoin.collector.collection.activity.withdrawHistory.WithdrawHistoryActivity;
import bitcoin.collector.collection.models.user.User;

import static bitcoin.collector.collection.database.FirestoreConstant.USER_TABLE;
import static bitcoin.collector.collection.util.AppUtil.clearAllIntent;
import static bitcoin.collector.collection.util.AppUtil.setDialogThemeButton;

public class LeftMenu {

    private static DrawerLayout mDrawerLayout;
    private static boolean isDrawerOpen = false;
    private static GoogleSignInClient signInClient;
    private static String privacyUrl = "https://aboutuscryptocurrency.blogspot.com/2021/05/policy-we-use-information-we-collect-or.html";
    private static String aboutUs = "https://aboutuscryptocurrency.blogspot.com/2021/05/us-we-are-digital-platform-that-helps.html";

    public static void leftMenu(final Activity activity, ImageView imgMenu) {
        mDrawerLayout = activity.findViewById(R.id.drawer_layout);
        TextView tvWithdraw = (TextView) activity.findViewById(R.id.textView56);
        TextView tvAbout = (TextView) activity.findViewById(R.id.textView57);
        TextView tvPrivacy = (TextView) activity.findViewById(R.id.textView58);
        TextView tvHelp = (TextView) activity.findViewById(R.id.textView59);
        TextView tvLogout = (TextView) activity.findViewById(R.id.textView60);
        ImageView imgClose = (ImageView) activity.findViewById(R.id.imageView4);
        Button btClose = (Button) activity.findViewById(R.id.button4);

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        tvWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                activity.startActivity(new Intent(activity, WithdrawHistoryActivity.class));
            }
        });
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(activity, AboutAndPrivacyActivity.class);
                intent.putExtra("isAbout", true);
                activity.startActivity(intent);
            }
        });
        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(activity, AboutAndPrivacyActivity.class);
                intent.putExtra("isAbout", false);
                activity.startActivity(intent);
            }
        });
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                activity.startActivity(new Intent(activity, HelpActivity.class));
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                openLogoutDialog(activity);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    public static boolean isDrawerOpen() {
        return isDrawerOpen;
    }

    public static void closeDrawer() {
        if(isDrawerOpen) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            isDrawerOpen = false;
        }
    }

    private static void openLogoutDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Do you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                updateBalanceInRef(activity);
                AppPreference.clearAllPreferences(activity);
                try {
                    FirebaseAuth.getInstance().signOut();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(activity.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    signInClient = GoogleSignIn.getClient(activity, gso);
                    signInClient.signOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, LoginActivity.class);
                clearAllIntent(intent);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setDialogThemeButton(activity, builder);
    }

    private static void updateBalanceInRef(Activity activity) {
        User mUser = AppPreference.getUserDetails(activity);
        Map<String, Object> map = new HashMap<>();
        map.put("fcmToken", "");
        map.put("coins", mUser.getCoins());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_TABLE)
                .document(mUser.getUserId())
                .update(map);
    }
}