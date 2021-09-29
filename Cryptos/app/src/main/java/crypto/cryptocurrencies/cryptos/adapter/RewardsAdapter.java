package crypto.cryptocurrencies.cryptos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.listener.RewardAdClickListener;
import crypto.cryptocurrencies.cryptos.models.reward.RewardData;

import java.util.ArrayList;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RewardData> rewardData;
    private RewardAdClickListener listener;

    public RewardsAdapter(Context context, ArrayList<RewardData> rewardData, RewardAdClickListener listener) {
        this.context = context;
        this.rewardData = rewardData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_reward,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        RewardData data = rewardData.get(position);

        holder.tvTitle.setText("Earn " + data.getCoins() + " Coins rewards");
        holder.tvDesc.setText("Get " + data.getCoins() + " coins rewards by watching " + data.getVideoCount() + " videos");

        if (!data.isEarned()) {
            holder.btCollect.setClickable(true);
            holder.btCollect.setAlpha(1.0f);
            holder.btCollect.setText("Get Coins");
        } else {
            holder.btCollect.setClickable(false);
            holder.btCollect.setAlpha(0.4f);
            holder.btCollect.setText("Collected");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRewardAdClick(position);
                }
            }
        });

        holder.btCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRewardAdClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewardData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvDesc;
        private Button btCollect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textView31);
            tvDesc = itemView.findViewById(R.id.textView32);
            btCollect = itemView.findViewById(R.id.button2);
        }
    }
}
