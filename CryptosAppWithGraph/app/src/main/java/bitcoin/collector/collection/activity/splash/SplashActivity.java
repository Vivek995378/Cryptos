package bitcoin.collector.collection.activity.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.activity.home.HomeActivity;
import bitcoin.collector.collection.activity.login.LoginActivity;
import bitcoin.collector.collection.util.AppPreference;

import static bitcoin.collector.collection.util.AppUtil.printHashKeyForFacebook;
import static bitcoin.collector.collection.util.AppUtil.saveFirebaseToken;
import static bitcoin.collector.collection.util.AppUtil.setHomeStatusTheme;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_splash);
            setHomeStatusTheme(this);

            saveFirebaseToken(this);
            printHashKeyForFacebook(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (AppPreference.isUserLoggedIn(SplashActivity.this)) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}