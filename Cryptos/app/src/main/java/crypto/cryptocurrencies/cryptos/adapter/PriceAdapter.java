package crypto.cryptocurrencies.cryptos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.activity.coinDetail.CoinDetailsActivity;
import crypto.cryptocurrencies.cryptos.models.price.PriceData;

import java.util.ArrayList;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PriceData> arrayList;
    private int number = 0;

    public PriceAdapter(Context context, ArrayList<PriceData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PriceData model = arrayList.get(position);

        holder.tvCoinCode.setText(model.getCoinCode());
//        holder.tvCoinName.setText(model.getCoinName());
        holder.tvCoinPrice.setText("$" + model.getPrice());
        if (model.isHigh()) {
            holder.tvCoinPercentage.setText("+" + String.valueOf(model.getPercentage()).replace("-", ""));
            holder.tvCoinPercentage.setTextColor(Color.GREEN);
        } else {
            holder.tvCoinPercentage.setText("-" + String.valueOf(model.getPercentage()).replace("-", ""));
            holder.tvCoinPercentage.setTextColor(Color.RED);
        }

        Glide.with(context).load(model.getCoinImage()).into(holder.imgCoin);

        try {
            holder.lineChartImage.setImageBitmap(model.getChartBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CoinDetailsActivity.class);
                intent.putExtra("coinCode", model.getCoinCode());
                intent.putExtra("coinName", model.getCoinName());
                intent.putExtra("coinImage", model.getCoinImage());
                intent.putExtra("adNumber", number);
                context.startActivity(intent);

                if (number == 5) {
                    number = 0;
                }
                number = number + 1;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCoinCode, tvCoinName, tvCoinPrice, tvCoinPercentage;
        private ImageView lineChartImage, imgCoin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCoinCode = itemView.findViewById(R.id.textView);
//            tvCoinName = itemView.findViewById(R.id.textView1);
            tvCoinPrice = itemView.findViewById(R.id.textView2);
            tvCoinPercentage = itemView.findViewById(R.id.textView3);
            lineChartImage = itemView.findViewById(R.id.lineChartImage);
            imgCoin = itemView.findViewById(R.id.imageView);
        }
    }

    public void updateAdapter(ArrayList<PriceData> priceData) {
        arrayList = priceData;
        notifyDataSetChanged();
    }
}
