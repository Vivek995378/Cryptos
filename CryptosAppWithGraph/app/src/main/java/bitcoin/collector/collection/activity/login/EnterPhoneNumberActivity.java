package bitcoin.collector.collection.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import bitcoin.collector.collection.R;
import com.hbb20.CountryCodePicker;

import bitcoin.collector.collection.activity.aboutAndPrivacy.AboutAndPrivacyActivity;

import static bitcoin.collector.collection.util.AppUtil.changeStatusBarColor;
import static bitcoin.collector.collection.util.AppUtil.hideKeyboard;

public class EnterPhoneNumberActivity extends AppCompatActivity {

    private EditText edtPhone;
    private CountryCodePicker countryCodePicker;
    private String privacyUrl = "https://aboutuscryptocurrency.blogspot.com/2021/05/policy-we-use-information-we-collect-or.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);
        changeStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        edtPhone = findViewById(R.id.editText);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.setDialogBackgroundColor(getResources().getColor(R.color.backgroundColor));
        countryCodePicker.setArrowColor(getResources().getColor(R.color.iconColor));
        countryCodePicker.setDialogTextColor(getResources().getColor(R.color.textColor));
        countryCodePicker.registerCarrierNumberEditText(edtPhone);
    }

    public void phonePrivacyPolicy(View view) {
        Intent intent = new Intent(this, AboutAndPrivacyActivity.class);
        intent.putExtra("isAbout", false);
        startActivity(intent);
    }

    public void verifyMobile(View view) {
        if (edtPhone.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
        } else {
            hideKeyboard(this);
            Intent intent = new Intent(this, OtpVerificationActivity.class);
            intent.putExtra("mobile", edtPhone.getText().toString());
            intent.putExtra("countryCode", countryCodePicker.getSelectedCountryCode());
            startActivity(intent);
        }
    }

    public void finish_phone(View view) {
        onBackPressed();
    }
}