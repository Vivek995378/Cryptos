package crypto.cryptocurrencies.cryptos.activity.home;

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
import android.view.View;
import android.widget.ImageView;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.activity.editProfile.EditProfileActivity;
import crypto.cryptocurrencies.cryptos.activity.splash.SplashActivity;
import crypto.cryptocurrencies.cryptos.alarmManager.NotificationReceiver;
import crypto.cryptocurrencies.cryptos.database.FirestoreConstant;
import crypto.cryptocurrencies.cryptos.fragment.ConverterFragment;
import crypto.cryptocurrencies.cryptos.fragment.PriceFragment;
import crypto.cryptocurrencies.cryptos.fragment.ProfileFragment;
import crypto.cryptocurrencies.cryptos.fragment.RewardsFragment;
import crypto.cryptocurrencies.cryptos.fragment.WithdrawFragment;
import crypto.cryptocurrencies.cryptos.models.home.HomeCoinCode;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.util.AppPreference;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static crypto.cryptocurrencies.cryptos.util.AppPreference.setUserDetails;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.clearAllIntent;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.setStatusBarGradiant;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private boolean isFirst = true, isSecond = false, isThird = false, isForth = false, isFifth = false,
            isBackPressed = true;

    private PriceFragment priceFragment;
    private ConverterFragment converterFragment;
    private RewardsFragment rewardsFragment;
    private WithdrawFragment withdrawFragment;
    private ProfileFragment profileFragment;
    private User mUser;
    private ImageView imgNotiAndEdit;
    public ArrayList<HomeCoinCode> coinCodeList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mUser = AppPreference.getUserDetails(this);
        setStatusBarGradiant(this);
        firestore = FirebaseFirestore.getInstance();

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

        imgNotiAndEdit = findViewById(R.id.imageView10);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        priceFragment = new PriceFragment(this);
        converterFragment = new ConverterFragment(this);
        rewardsFragment = new RewardsFragment();
        withdrawFragment = new WithdrawFragment(this);
        profileFragment = new ProfileFragment();

        bottomNavigationView.setSelectedItemId(R.id.item_bottom_first);
        loadFragment(priceFragment, isBackPressed);
        setUserDetailsInRef();

        imgNotiAndEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFifth) {
                    startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                }
            }
        });
     //   addCoinsList();
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
                                setUserDetails(HomeActivity.this, mUser);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        try {
            profileFragment.setUserDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkInAppUpdate();
        super.onResume();
    }

    private void checkInAppUpdate() {
        final AppUpdateManager manager = AppUpdateManagerFactory.create(HomeActivity.this);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_bottom_first:
                if (!isFirst) {
                    imgNotiAndEdit.setVisibility(View.GONE);
                    isFirst = true;
                    isSecond = false;
                    isThird = false;
                    isForth = false;
                    isFifth = false;
                    loadFragment(priceFragment, isBackPressed);
                }
                break;
            case R.id.item_bottom_second:
                if (!isSecond) {
                    imgNotiAndEdit.setVisibility(View.GONE);
                    isFirst = false;
                    isSecond = true;
                    isThird = false;
                    isForth = false;
                    isFifth = false;
                    loadFragment(withdrawFragment, isBackPressed);
                    try {
                        withdrawFragment.setMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.item_bottom_third:
                if (!isThird) {
                    imgNotiAndEdit.setVisibility(View.GONE);
                    isFirst = false;
                    isSecond = false;
                    isThird = true;
                    isForth = false;
                    isFifth = false;
                    loadFragment(converterFragment, isBackPressed);
                }
                break;
            case R.id.item_bottom_forth:
                if (!isForth) {
                    imgNotiAndEdit.setVisibility(View.GONE);
                    isFirst = false;
                    isSecond = false;
                    isThird = false;
                    isForth = true;
                    isFifth = false;
                    loadFragment(rewardsFragment, isBackPressed);
                    mUser = AppPreference.getUserDetails(HomeActivity.this);
                    try {
                        if (rewardsFragment.tvCoins != null) {
                            rewardsFragment.tvCoins.setText(String.valueOf(mUser.getCoins()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.item_bottom_fifth:
                if (!isFifth) {
                    imgNotiAndEdit.setVisibility(View.VISIBLE);
                    isFirst = false;
                    isSecond = false;
                    isThird = false;
                    isForth = false;
                    isFifth = true;
                    loadFragment(profileFragment, isBackPressed);
                }
                break;
        }
        return true;
    }

    private void addCoinsList() {
        Map<String, Object> map = new HashMap<>();
        map.put("coinCode", "BTC");
        map.put("coinImage", "https://firebasestorage.googleapis.com/v0/b/cryptos-29d54.appspot.com/o/btc.png?alt=media&token=317cb9c4-1f57-486d-822f-a82177d28303");
        map.put("coinName", "Bitcoin");
        map.put("position", 1);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "ETH");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746238/eth.png?width=200");
        map.put("coinName", "Ethereum");
        map.put("position", 2);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "BNB");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746880/bnb.png?width=200");
        map.put("coinName", "Binance Coin");
        map.put("position", 3);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "ADA");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746235/ada.png?width=200");
        map.put("coinName", "Cardano");
        map.put("position", 4);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "DOGE");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746339/doge.png?width=200");
        map.put("coinName", "Dogecoin");
        map.put("position", 5);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "USDC");
        map.put("coinImage", "https://www.cryptocompare.com/media/34835941/usdc.png?width=200");
        map.put("coinName", "USD Coin");
        map.put("position", 6);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "DOT");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746348/dot.png?width=200");
        map.put("coinName", "Polkadot");
        map.put("position", 7);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "LTC");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746243/ltc.png?width=200");
        map.put("coinName", "Litecoin");
        map.put("position", 8);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "USDT");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746338/usdt.png?width=200");
        map.put("coinName", "Tether");
        map.put("position", 9);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "XRP");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746347/xrp.png?width=200");
        map.put("coinName", "Ripple");
        map.put("position", 10);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "LINK");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746242/link.png?width=200");
        map.put("coinName", "Chainlink");
        map.put("position", 11);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "BCH");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746245/bch.png?width=200");
        map.put("coinName", "Bitcoin Cash");
        map.put("position", 12);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "BUSD");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746248/busd.png?width=200");
        map.put("coinName", "Ethereum");
        map.put("position", 13);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "ZEC");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746899/zec.png?width=200");
        map.put("coinName", "ZCash");
        map.put("position", 14);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "OKB");
        map.put("coinImage", "https://www.cryptocompare.com/media/37747532/okb.png?width=200");
        map.put("coinName", "Okex");
        map.put("position", 15);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "THETA");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746670/theta.png?width=200");
        map.put("coinName", "Theta");
        map.put("position", 16);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "DASH");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746893/dash.png?width=200");
        map.put("coinName", "Dash");
        map.put("position", 17);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "NEO");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746892/neo.png?width=200");
        map.put("coinName", "NEO");
        map.put("position", 18);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "SHIB");
        map.put("coinImage", "https://www.cryptocompare.com/media/37747199/shib.png?width=200");
        map.put("coinName", "Shiba Inu");
        map.put("position", 19);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "SAFEMOON");
        map.put("coinImage", "https://www.cryptocompare.com/media/37747526/safemoon.png?width=200");
        map.put("coinName", "SafeMoon");
        map.put("position", 20);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "USDT");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746338/usdt.png?width=200");
        map.put("coinName", "Tether");
        map.put("position", 21);
        firestore.collection("cryptoList").document().set(map);
        map = new HashMap<>();
        map.put("coinCode", "TRX");
        map.put("coinImage", "https://www.cryptocompare.com/media/37746879/trx.png?width=200");
        map.put("coinName", "Tron");
        map.put("position", 22);
        firestore.collection("cryptoList").document().set(map);
    }
}