package crypto.cryptocurrencies.cryptos.activity.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cryptos.cryptocurrency.R;

import crypto.cryptocurrencies.cryptos.activity.aboutAndPrivacy.AboutAndPrivacyActivity;
import crypto.cryptocurrencies.cryptos.activity.home.HomeActivity;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static crypto.cryptocurrencies.cryptos.database.FirestoreConstant.USER_TABLE;
import static crypto.cryptocurrencies.cryptos.util.AppPreference.getFirebaseToken;
import static crypto.cryptocurrencies.cryptos.util.AppPreference.setUserDetails;
import static crypto.cryptocurrencies.cryptos.util.AppPreference.setUserLoggedIn;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.hideProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.setStatusBarGradiant;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.showProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.updateFcmToken;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient signInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 101;
    private CallbackManager mCallbackManager;
    private LoginButton fbLoginButton;
    private boolean isFacebook = false, isGoogle = false;
    private static final String EMAIL = "email";
    private String privacyUrl = "https://aboutuscryptocurrency.blogspot.com/2021/05/policy-we-use-information-we-collect-or.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarGradiant(this);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(this);
    }

    public void loginPrivacyPolicy(View view) {
        Intent intent = new Intent(this, AboutAndPrivacyActivity.class);
        intent.putExtra("isAbout", false);
        startActivity(intent);
    }

    public void startFacebookLogin(View view) {
        showProgressDialog(this);
        isGoogle = false;
        isFacebook = true;
        fbLoginButton = new LoginButton(this);
        fbLoginButton.setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        setFacebookCallback();
        fbLoginButton.performClick();
    }

    private void setFacebookCallback() {
        fbLoginButton.setPermissions(Arrays.asList(EMAIL));
        mCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, "Facebook cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                hideProgressDialog();
                Toast.makeText(LoginActivity.this, "Facebook Exception: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUser(user.getUid(), user.getEmail());
                        } else {
                            Toast.makeText(LoginActivity.this, "Facebook Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void startPhoneLogin(View view) {
        startActivity(new Intent(this, EnterPhoneNumberActivity.class));
    }

    public void startGoogleLogin(View view) {
        showProgressDialog(this);
        configureGoogleClient();
        isGoogle = true;
        isFacebook = false;
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityIfNeeded(signInIntent, RC_SIGN_IN);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configureGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUser(user.getUid(), user.getEmail());
                        } else {
                            Toast.makeText(LoginActivity.this, "Firebase Authentication failed:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isFacebook) {
            isFacebook = false;
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (isGoogle) {
            isGoogle = false;
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    hideProgressDialog();
                    Toast.makeText(this, "Google Sign in Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkUser(final String uid, final String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_TABLE)
                .document(uid)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hideProgressDialog();
                        if (documentSnapshot.exists()) {
                            loginUser(uid);
                        } else {
                            registerUser(uid, email);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String uId, String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("userId", uId);
        map.put("fcmToken", getFirebaseToken(this));
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_TABLE)
                .document(uId)
                .set(map);
        loginUser(uId);
    }

    private void loginUser(final String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_TABLE)
                .document(uid)
                .get()
                .addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hideProgressDialog();
                        if (documentSnapshot.exists()) {
                            updateFcmToken(LoginActivity.this, uid);
                            User user = documentSnapshot.toObject(User.class);
                            setUserDetails(LoginActivity.this, user);
                            setUserLoggedIn(LoginActivity.this, true);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            AppUtil.clearAllIntent(intent);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ooops! Something went wrong, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}