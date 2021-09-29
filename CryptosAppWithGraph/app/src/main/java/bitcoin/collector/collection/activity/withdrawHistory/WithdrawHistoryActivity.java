package bitcoin.collector.collection.activity.withdrawHistory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.adapter.WithdrawHistoryAdapter;
import bitcoin.collector.collection.database.FirestoreConstant;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.models.withdrawHistory.WithdrawHistoryData;
import bitcoin.collector.collection.util.AppPreference;
import bitcoin.collector.collection.util.AppUtil;
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
        AppUtil.setStatusTheme(this);
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