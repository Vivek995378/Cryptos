package crypto.cryptocurrencies.cryptos.activity.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptos.cryptocurrency.R;

import crypto.cryptocurrencies.cryptos.activity.aboutAndPrivacy.AboutAndPrivacyActivity;
import crypto.cryptocurrencies.cryptos.activity.home.HomeActivity;
import crypto.cryptocurrencies.cryptos.database.FirestoreConstant;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.otpReceiver.SmsBroadcastReceiver;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static crypto.cryptocurrencies.cryptos.util.AppPreference.getFirebaseToken;
import static crypto.cryptocurrencies.cryptos.util.AppPreference.setUserDetails;
import static crypto.cryptocurrencies.cryptos.util.AppPreference.setUserLoggedIn;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.setStatusBarGradiant;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextView tvMobileAppend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode, mobile, countryCode;
    private FirebaseAuth mAuth;
    private EditText edtOtp;
    private FirebaseFirestore db;
    private SmsBroadcastReceiver otpReceiver;
    private static final int REQ_USER_CONSENT = 200;
    private String enteredOtp = "";
    private boolean isAutoVerify = false;
    private String privacyUrl = "https://aboutuscryptocurrency.blogspot.com/2021/05/policy-we-use-information-we-collect-or.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        setStatusBarGradiant(this);

        mAuth = FirebaseAuth.getInstance();
        mobile = getIntent().getStringExtra("mobile");
        countryCode = getIntent().getStringExtra("countryCode");
        db = FirebaseFirestore.getInstance();
        edtOtp = findViewById(R.id.editText1);
        tvMobileAppend = findViewById(R.id.textView7);

        tvMobileAppend.append(" +" + countryCode + "-" + mobile);

        if (AppUtil.isInternetAvailable(this)) {
            AppUtil.showProgressDialog(this);
            sendOtp();
            verifyMobile();
        } else {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        startSmsUserConsent();
    }

    public void otpPrivacyPolicy(View view) {
        Intent intent = new Intent(this, AboutAndPrivacyActivity.class);
        intent.putExtra("isAbout", false);
        startActivity(intent);
    }

    public void verifyOtp(View view) {
        AppUtil.hideKeyboard(OtpVerificationActivity.this);
        try {
            enteredOtp = edtOtp.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (verificationCode != null && enteredOtp != null && enteredOtp.length() == 6) {
            if (AppUtil.isInternetAvailable(OtpVerificationActivity.this)) {
                AppUtil.showProgressDialog(OtpVerificationActivity.this);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                SignInWithPhone(credential, false);
            } else {
                Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQ_USER_CONSENT) {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                    String[] otpReceived = message.split(" ");
                    final String otp = otpReceived[0];
                    if (otp != null) {
                        edtOtp.setText(otp);
                    }
                    isAutoVerify = true;
                    try {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (AppUtil.isInternetAvailable(OtpVerificationActivity.this)) {
                                    AppUtil.hideKeyboard(OtpVerificationActivity.this);
                                    AppUtil.showProgressDialog(OtpVerificationActivity.this);
                                    try {
                                        if (otpReceiver != null) {
                                            unregisterReceiver(otpReceiver);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        PhoneAuthCredential credential = PhoneAuthProvider
                                                .getCredential(verificationCode, otp);
                                        SignInWithPhone(credential, false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(OtpVerificationActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerBroadcastReceiver() {
        otpReceiver = new SmsBroadcastReceiver();
        otpReceiver.smsBroadcastReceiverListener =
                new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityIfNeeded(intent, REQ_USER_CONSENT);
                    }

                    @Override
                    public void onFailure() {
                    }
                };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(otpReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(otpReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendOtp() {
        mAuth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                SignInWithPhone(phoneAuthCredential, true);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                AppUtil.hideProgressDialog();
                Toast.makeText(OtpVerificationActivity.this, "OTP Sent failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                AppUtil.hideProgressDialog();
                verificationCode = s;
                Toast.makeText(OtpVerificationActivity.this, "OTP has been sent on " + mobile, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void verifyMobile() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+" + countryCode + mobile)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallback)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void SignInWithPhone(PhoneAuthCredential credential, boolean isAuto) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUser(mAuth.getCurrentUser().getUid());
                        } else {
                            AppUtil.hideProgressDialog();
                            if(!isAuto) {
                                Toast.makeText(OtpVerificationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AppUtil.hideProgressDialog();
                if(!isAuto) {
                    Toast.makeText(OtpVerificationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUser(String uid) {
        db.collection(FirestoreConstant.USER_TABLE)
                .document(uid)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AppUtil.hideProgressDialog();
                        if (documentSnapshot.exists()) {
                            loginUser(mAuth.getCurrentUser().getUid());
                        } else {
                            registerUser(mAuth.getCurrentUser().getUid());
                        }
                    }
                });
    }

    private void registerUser(String uId) {
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mobile);
        map.put("userId", uId);
        map.put("fcmToken", getFirebaseToken(this));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FirestoreConstant.USER_TABLE)
                .document(uId)
                .set(map);
        loginUser(uId);
    }

    private void loginUser(final String uid) {
        AppUtil.showProgressDialog(OtpVerificationActivity.this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirestoreConstant.USER_TABLE)
                .document(uid)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AppUtil.hideProgressDialog();
                        if (documentSnapshot.exists()) {
                            AppUtil.updateFcmToken(OtpVerificationActivity.this, uid);
                            User user = documentSnapshot.toObject(User.class);
                            setUserDetails(OtpVerificationActivity.this, user);
                            setUserLoggedIn(OtpVerificationActivity.this, true);
                            Intent intent = new Intent(OtpVerificationActivity.this, HomeActivity.class);
                            AppUtil.clearAllIntent(intent);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, "Ooops! Something went wrong, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (otpReceiver != null) {
                unregisterReceiver(otpReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}