package crypto.cryptocurrencies.cryptos.activity.withdrawHistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.adapter.WithdrawHistoryAdapter;
import crypto.cryptocurrencies.cryptos.database.FirestoreConstant;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.models.withdrawHistory.WithdrawHistoryData;
import crypto.cryptocurrencies.cryptos.util.AppPreference;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WithdrawHistoryActivity extends AppCompatActivity {

    private LinearLayout llTop;
    private RecyclerView rvHistory;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_history);
        AppUtil.setStatusBarGradiant(this);
        mUser = AppPreference.getUserDetails(this);

        llTop = findViewById(R.id.linearLayout9);
        rvHistory = findViewById(R.id.recyclerView2);

        getData();
    }

    private void getData() {
        AppUtil.showProgressDialog(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FirestoreConstant.WITHDRAW_TABLE)
                .whereEqualTo("userId", mUser.getUserId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                AppUtil.hideProgressDialog();
                try {
                    if (task.isSuccessful()) {
                        ArrayList<WithdrawHistoryData> arrayList = new ArrayList<>();
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            arrayList.add(snapshot.toObject(WithdrawHistoryData.class));
                        }
                        if (arrayList.size() == 0) {
                            Toast.makeText(WithdrawHistoryActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                        } else {
                            llTop.setVisibility(View.VISIBLE);
                            WithdrawHistoryAdapter adapter = new WithdrawHistoryAdapter(WithdrawHistoryActivity.this, arrayList);
                            rvHistory.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(WithdrawHistoryActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void backWithdrawHistory(View view) {
        onBackPressed();
    }
}