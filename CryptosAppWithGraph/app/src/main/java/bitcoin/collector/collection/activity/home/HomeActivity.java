package bitcoin.collector.collection.activity.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.activity.splash.SplashActivity;
import bitcoin.collector.collection.alarmManager.NotificationReceiver;
import bitcoin.collector.collection.database.FirestoreConstant;
import bitcoin.collector.collection.fragment.ConverterFragment;
import bitcoin.collector.collection.fragment.PriceFragment;
import bitcoin.collector.collection.fragment.ProfileFragment;
import bitcoin.collector.collection.fragment.RewardsFragment;
import bitcoin.collector.collection.fragment.WithdrawFragment;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.util.AppPreference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import static bitcoin.collector.collection.util.AppUtil.changeStatusBarColor;
import static bitcoin.collector.collection.util.AppUtil.clearAllIntent;
import static bitcoin.collector.collection.util.AppUtil.formatValue;
import static bitcoin.collector.collection.util.LeftMenu.leftMenu;

public class HomeActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private boolean isFirst = true, isSecond = false, isThird = false, isForth = false, isFifth = false,
            isBackPressed = true;
    private String bottomSelected = "";
    private PriceFragment priceFragment;
    private ConverterFragment converterFragment;
    private RewardsFragment rewardsFragment;
    private WithdrawFragment withdrawFragment;
    private ProfileFragment profileFragment;
    private User mUser;
    private TextView tvTop;
    private ImageView imgMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mUser = AppPreference.getUserDetails(this);
        changeStatusBarColor(this, this.getResources().getColor(R.color.colorPrimary), true);
        bottomSelected = getResources().getString(R.string.app_name);
        imgMenu = findViewById(R.id.imageView9);
        tvTop = findViewById(R.id.textView8);
        tvTop.setText(bottomSelected);

        leftMenu(this, imgMenu);

        try {
            AppLovinSdk.getInstance(HomeActivity.this)
                    .setMediationProvider("max");
            AppLovinSdk.initializeSdk(HomeActivity.this, new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        priceFragment = new PriceFragment(this);
        converterFragment = new ConverterFragment(this);
        rewardsFragment = new RewardsFragment();
        withdrawFragment = new WithdrawFragment(this);
        profileFragment = new ProfileFragment();

        bottomNavigationView.setSelectedItemId(R.id.item_bottom_first);
        loadFragment(priceFragment, isBackPressed);
        setUserDetailsInRef();
        createNotificationChannel();
        setAlarm(10, 0, "10AM");
    }

    private void loadFragment(Fragment fragment, boolean addBack) {
        if (fragment != null) {
            String tag = fragment.getClass().getSimpleName();
            FragmentTransaction tr = fragmentManager.beginTransaction();

            Fragment curFrag = getSupportFragmentManager().getPrimaryNavigationFragment();
            final Fragment cacheFrag = getSupportFragmentManager().findFragmentByTag(tag);

            if (curFrag != null)
                tr.hide(curFrag);

            if (cacheFrag == null) {
                tr.add(R.id.container, fragment, tag);
            } else {
                tr.show(cacheFrag);
                fragment = cacheFrag;
            }

            tr.setPrimaryNavigationFragment(fragment);
            if (addBack) {
                tr.addToBackStack(tag);
            }
            tr.commit();
            isBackPressed = true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_bottom_first:
                if (!isFirst) {
                    isFirst = true;
                    isSecond = false;
                    isThird = false;
                    isForth = false;
                    isFifth = false;
                    bottomSelected = getResources().getString(R.string.app_name);
                    tvTop.setText(bottomSelected);
                    loadFragment(priceFragment, isBackPressed);
                }
                break;
            case R.id.item_bottom_second:
                if (!isSecond) {
                    isFirst = false;
                    isSecond = true;
                    isThird = false;
                    isForth = false;
                    isFifth = false;
                    bottomSelected = "Withdraw";
                    tvTop.setText(bottomSelected);
                    loadFragment(withdrawFragment, isBackPressed);
                }
                break;
            case R.id.item_bottom_third:
                if (!isThird) {
                    isFirst = false;
                    isSecond = false;
                    isThird = true;
                    isForth = false;
                    isFifth = false;
                    bottomSelected = "Convert";
                    tvTop.setText(bottomSelected);
                    loadFragment(converterFragment, isBackPressed);
                }
                break;
            case R.id.item_bottom_forth:
                if (!isForth) {
                    isFirst = false;
                    isSecond = false;
                    isThird = false;
                    isFifth = false;
                    isForth = true;
                    bottomSelected = "Reward";
                    tvTop.setText(bottomSelected);
                    loadFragment(rewardsFragment, isBackPressed);
                    mUser = AppPreference.getUserDetails(this);
                    try {
                        if (rewardsFragment.tvCoins != null) {
                            rewardsFragment.tvCoins.setText(formatValue(mUser.getCoins()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.item_bottom_fifth:
                if(!isFifth){
                    isFirst = false;
                    isSecond = false;
                    isThird = false;
                    isForth = false;
                    isFifth = true;
                    bottomSelected = "Profile";
                    tvTop.setText(bottomSelected);
                    loadFragment(profileFragment, isBackPressed);
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count <= 1) {
            finish();
            super.onBackPressed();
        } else {
            String stackName = fragmentManager.getBackStackEntryAt(count - 2).getName();
            isBackPressed = false;
            assert stackName != null;
            switch (stackName) {
                case "PriceFragment":
                    bottomNavigationView.setSelectedItemId(R.id.item_bottom_first);
                    break;
                case "WithdrawFragment":
                    bottomNavigationView.setSelectedItemId(R.id.item_bottom_second);
                    break;
                case "ConverterFragment":
                    bottomNavigationView.setSelectedItemId(R.id.item_bottom_third);
                    break;
                case "RewardsFragment":
                    bottomNavigationView.setSelectedItemId(R.id.item_bottom_forth);
                    break;
                case "ProfileFragment":
                    bottomNavigationView.setSelectedItemId(R.id.item_bottom_fifth);
                    break;
            }
            fragmentManager.popBackStack();
        }
    }

    private void setUserDetailsInRef() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FirestoreConstant.USER_TABLE)
                .document(mUser.getUserId().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            if (task.getResult().exists()) {
                                mUser = task.getResult().toObject(User.class);
                                AppPreference.setUserDetails(HomeActivity.this, mUser);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        checkInAppUpdate();
        tvTop.setText(bottomSelected);
        try{
            if(profileFragment != null) {
                profileFragment.setUserDetails();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onResume();
    }
    private void checkInAppUpdate() {
        final AppUpdateManager manager;
        manager = AppUpdateManagerFactory.create(HomeActivity.this);
        com.google.android.play.core.tasks.Task<AppUpdateInfo> infoTask = manager.getAppUpdateInfo();

        infoTask.addOnCompleteListener(new com.google.android.play.core.tasks.OnCompleteListener<AppUpdateInfo>() {
            @Override
            public void onComplete(@NonNull com.google.android.play.core.tasks.Task<AppUpdateInfo> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && task.getResult().isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        try {
                            manager.startUpdateFlowForResult(task.getResult(), AppUpdateType.IMMEDIATE,
                                    HomeActivity.this, 1001);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode != RESULT_OK) {

            } else {
                Intent intent = new Intent(this, SplashActivity.class);
                clearAllIntent(intent);
                startActivity(intent);
                finish();
            }
        }

        if(resultCode == 1005){
            try{
                if(priceFragment != null) {
                    priceFragment.setUserDetails();
                }
                if(profileFragment != null) {
                    profileFragment.setUserDetails();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.app_name);
            String desc = "Grab Free " + getResources().getString(R.string.app_name) + " and Withdraw Directly in Wallet";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel1", name, importance);
            channel.setDescription(desc);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void setAlarm(int hour, int min, String time) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("time", time);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        if (Build.VERSION.SDK_INT >= 23) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}