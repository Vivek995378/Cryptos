package bitcoin.collector.collection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bitcoin.collector.collection.R;
import bitcoin.collector.collection.models.bitcoin.BitcoinData;

public class PriceTopCountriesAdapter extends RecyclerView.Adapter<PriceTopCountriesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BitcoinData> arrayList;

    public PriceTopCountriesAdapter(Context context, ArrayList<BitcoinData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_country_prices,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BitcoinData model = arrayList.get(position);
        holder.tvCountryName.setText(model.getCountryName());
        holder.tvPrice.setText(model.getPrice());
        holder.tvLogo.setText(model.getCountryFlag());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCountryName, tvPrice, tvLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCountryName = itemView.findViewById(R.id.textView4);
            tvPrice = itemView.findViewById(R.id.textView8);
            tvLogo = itemView.findViewById(R.id.textView3);
        }
    }
}