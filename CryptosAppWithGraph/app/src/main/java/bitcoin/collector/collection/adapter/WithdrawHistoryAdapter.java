package bitcoin.collector.collection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.models.withdrawHistory.WithdrawHistoryData;
import bitcoin.collector.collection.util.AppUtil;

import java.util.ArrayList;

public class WithdrawHistoryAdapter extends RecyclerView.Adapter<WithdrawHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WithdrawHistoryData> arrayList;

    public WithdrawHistoryAdapter(Context context, ArrayList<WithdrawHistoryData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_withdraw_history,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WithdrawHistoryData data = arrayList.get(position);

        holder.tvDate.setText(AppUtil.getDateByMilliSeconds(data.getCreationDate()*1000, "MMM dd, YYYY"));
        holder.tvCoinValue.setText(data.getCoinValue());
        holder.tvCoinName.setText(data.getCoinName());
        holder.tvWalletAddress.setText("To: "+data.getWalletAddress());
        holder.tvPaidAmount.setText("$" + data.getPaidAmount());
        holder.tvStatus.setText(data.getStatus());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate, tvCoinValue, tvCoinName, tvWalletAddress, tvPaidAmount, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.textView43);
            tvCoinValue = itemView.findViewById(R.id.textView44);
            tvCoinName = itemView.findViewById(R.id.textView48);
            tvWalletAddress = itemView.findViewById(R.id.textView45);
            tvPaidAmount = itemView.findViewById(R.id.textView46);
            tvStatus = itemView.findViewById(R.id.textView47);
        }
    }
}
