package crypto.cryptocurrencies.cryptos.activity.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.activity.home.HomeActivity;
import crypto.cryptocurrencies.cryptos.activity.login.LoginActivity;
import crypto.cryptocurrencies.cryptos.util.AppPreference;

import static crypto.cryptocurrencies.cryptos.util.AppUtil.printHashKeyForFacebook;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.saveFirebaseToken;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_splash);
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