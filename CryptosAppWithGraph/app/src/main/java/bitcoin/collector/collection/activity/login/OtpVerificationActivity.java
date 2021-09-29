package bitcoin.collector.collection.activity.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.activity.aboutAndPrivacy.AboutAndPrivacyActivity;
import bitcoin.collector.collection.activity.home.HomeActivity;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.util.AppUtil;
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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bitcoin.collector.collection.util.AppPreference;

import static bitcoin.collector.collection.database.FirestoreConstant.USER_TABLE;
import static bitcoin.collector.collection.util.AppUtil.changeStatusBarColor;
import static bitcoin.collector.collection.util.AppUtil.hideKeyboard;
import static bitcoin.collector.collection.util.AppUtil.hideProgressDialog;
import static bitcoin.collector.collection.util.AppUtil.isInternetAvailable;
import static bitcoin.collector.collection.util.AppUtil.showProgressDialog;
import static bitcoin.collector.collection.util.AppUtil.updateFcmToken;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextView tvMobileAppend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode, mobile, countryCode;
    private FirebaseAuth mAuth;
    private EditText edtOtp;
    private FirebaseFirestore db;
    private String enteredOtp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        changeStatusBarColor(this, getResources().getColor(R.color.colorPrimary), true);
        mAuth = FirebaseAuth.getInstance();
        mobile = getIntent().getStringExtra("mobile");
        countryCode = getIntent().getStringExtra("countryCode");
        db = FirebaseFirestore.getInstance();
        edtOtp = findViewById(R.id.editText1);
        tvMobileAppend = findViewById(R.id.textView7);

        tvMobileAppend.append(" +"+countryCode + "-" + mobile);

        if (isInternetAvailable(this)) {
            showProgressDialog(this);
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
        hideKeyboard(OtpVerificationActivity.this);
        try {
            enteredOtp = edtOtp.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (verificationCode != null && enteredOtp != null && enteredOtp.length() == 6) {
            if (isInternetAvailable(OtpVerificationActivity.this)) {
                showProgressDialog(OtpVerificationActivity.this);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
                SignInWithPhone(credential);
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

    private void sendOtp() {
        mAuth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                hideProgressDialog();
                Toast.makeText(OtpVerificationActivity.this, "OTP Sent failed, Please try again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                hideProgressDialog();
                verificationCode = s;
                Toast.makeText(OtpVerificationActivity.this, "OTP has been sent on " + mobile, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void verifyMobile() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+" + countryCode + mobile,                     // Phone number to verify
                2,                           // Timeout duration
                TimeUnit.MINUTES,                // Unit of timeout
                OtpVerificationActivity.this,        // Activity (for callback binding)
                mCallback);
    }

    private void SignInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUser(mAuth.getCurrentUser().getUid());
                        } else {
                            hideProgressDialog();
                            Toast.makeText(OtpVerificationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(OtpVerificationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser(String uid) {
        db.collection(USER_TABLE)
                .document(uid)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hideProgressDialog();
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
        map.put("fcmToken", AppPreference.getFirebaseToken(this));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_TABLE)
                .document(uId)
                .set(map);
        loginUser(uId);
    }

    private void loginUser(final String uid) {
        showProgressDialog(OtpVerificationActivity.this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_TABLE)
                .document(uid)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hideProgressDialog();
                        if (documentSnapshot.exists()) {
                            updateFcmToken(OtpVerificationActivity.this, uid);
                            User user = documentSnapshot.toObject(User.class);
                            AppPreference.setUserDetails(OtpVerificationActivity.this, user);
                            AppPreference.setUserLoggedIn(OtpVerificationActivity.this, true);
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
}