package bitcoin.collector.collection.fragment;

import android.annotation.SuppressLint;
import android.icu.text.DecimalFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

import bitcoin.collector.collection.R;

import bitcoin.collector.collection.activity.home.HomeActivity;
import bitcoin.collector.collection.adapter.PriceAdapter;
import bitcoin.collector.collection.adapter.PriceTopCountriesAdapter;
import bitcoin.collector.collection.models.bitcoin.BitcoinData;
import bitcoin.collector.collection.models.user.User;
import bitcoin.collector.collection.retrofit.ApiInterface;
import bitcoin.collector.collection.retrofit.RetrofitClient;
import bitcoin.collector.collection.util.AppPreference;
import bitcoin.collector.collection.util.AppUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static bitcoin.collector.collection.util.AppPreference.getUserDetails;
import static bitcoin.collector.collection.util.AppUtil.hideProgressDialog;
import static bitcoin.collector.collection.util.AppUtil.showProgressDialog;


public class PriceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private EditText edtSearch;
    private RecyclerView rvPrice;
    private RecyclerView rvPriceCountry;
    private ArrayList<BitcoinData> priceList;
    private ArrayList<BitcoinData> countryPriceList;
    private PriceAdapter priceAdapter;
    private SwipeRefreshLayout refreshLayout;
    private HomeActivity homeActivity;
    private MaxAdView bannerAdView;
    private String countryString = "";
    private FirebaseRemoteConfig mRemoteConfig;
    private JSONArray obj;
    private User mUser;
    private ImageView imgUser;
    private TextView tvName;
    private PriceTopCountriesAdapter countriesAdapter;

    private MaxAdViewAdListener bannerAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdLoaded(MaxAd ad) {
            bannerAdView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError errorCode) {
            try {
                bannerAdView.loadAd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {

        }

        @Override
        public void onAdHidden(MaxAd ad) {

        }

        @Override
        public void onAdClicked(MaxAd ad) {

        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError errorCode) {

        }

        @Override
        public void onAdExpanded(MaxAd ad) {

        }

        @Override
        public void onAdCollapsed(MaxAd ad) {

        }
    };

    public PriceFragment() {
        // Required empty public constructor
    }

    public PriceFragment(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);
        mUser = getUserDetails(getActivity());
        edtSearch = view.findViewById(R.id.editText6);
        rvPrice = view.findViewById(R.id.recyclerView);
        rvPriceCountry = view.findViewById(R.id.recyclerView3);
        bannerAdView = view.findViewById(R.id.maxAdView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);

        try {
            bannerAdView.setListener(bannerAdListener);
            bannerAdView.loadAd();
            bannerAdView.startAutoRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

        priceList = new ArrayList<>();
        countryPriceList = new ArrayList<>();
        countriesAdapter = new PriceTopCountriesAdapter(getActivity(), countryPriceList);
        priceAdapter = new PriceAdapter(getActivity(), priceList);
        rvPrice.setAdapter(priceAdapter);
        rvPriceCountry.setAdapter(countriesAdapter);
        if (!AppUtil.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else {
            mRemoteConfig = FirebaseRemoteConfig.getInstance();
            getRemoteConfigData();
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().isEmpty()) {
                    priceAdapter.updateAdapter(priceList);
                } else {
                    filterCategoryList(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setUserDetails();
        return view;
    }

    private void getRemoteConfigData() {
        showProgressDialog(getActivity());
        mRemoteConfig.fetch(1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        try {
                            mRemoteConfig.activate();
                            obj = new JSONArray(mRemoteConfig.getString("country_details"));
                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject jsonObject = obj.getJSONObject(i);
                                if (countryString.length() > 490 && countryString.length() < 495) {
                                    break;
                                } else if (countryString.isEmpty()) {
                                    countryString = jsonObject.getString("code");
                                } else {
                                    countryString = countryString + "," + jsonObject.getString("code");
                                }
                            }
                            getData();
                        } catch (JSONException e) {
                            hideProgressDialog();
                            getRemoteConfigData();
                        }
                    }
                });
    }

    @SuppressLint("NewApi")
    private void getData() {
        ApiInterface apiInterface = RetrofitClient.getBitcoinRetrofitInstance().create(ApiInterface.class);
        apiInterface.getData("BTC", countryString)
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        try {
                            JSONObject resultObj = new JSONObject(response.body().toString());
                            for (int i = 0; i < obj.length(); i++) {
                                JSONObject jsonObject = obj.getJSONObject(i);
                                try {
                                    if (resultObj.getString(jsonObject.getString("code")) != null) {
                                        CountryCode countryCode = CountryCode.getByCode(CurrencyCode.getByCode(jsonObject.getString("code")).getNumeric());
                                        if (countryCode != null) {
                                            DecimalFormat REAL_FORMATTER = new DecimalFormat("0.########");
                                            double price = Double.parseDouble(resultObj.getString(jsonObject.getString("code")));
                                            BitcoinData data = new BitcoinData(countryCode.getName(),
                                                    jsonObject.getString("symbol") + " " +
                                                            REAL_FORMATTER.format(price),
                                                    jsonObject.getString("emoji"));
                                            priceList.add(data);
//                                            if (i < 10) {
//                                                countryPriceList.add(data);
//                                            } else {
//                                                priceList.add(data);
//                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            countriesAdapter.notifyDataSetChanged();
                            priceAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                    }
                });
    }

    private void filterCategoryList(String value) {
        ArrayList<BitcoinData> tempList = new ArrayList<>();

        for (BitcoinData s : priceList) {
            if (s.getCountryName().toLowerCase().contains(value.toLowerCase())
                    && !tempList.toString().contains(s.getCountryName())) {
                tempList.add(s);
            }
        }

        HashSet<BitcoinData> hashSet = new HashSet<>();
        hashSet.addAll(tempList);
        tempList.clear();
        tempList.addAll(hashSet);
        priceAdapter.updateAdapter(tempList);
    }

    @Override
    public void onRefresh() {
        try {
            refreshLayout.setRefreshing(false);
            if (AppUtil.isInternetAvailable(getActivity())) {
                priceList.clear();
                countryPriceList.clear();
                countriesAdapter.notifyDataSetChanged();
                priceAdapter.notifyDataSetChanged();
                getData();
            } else {
                Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserDetails()
    {
        mUser = AppPreference.getUserDetails(getActivity());
        try {
            if (mUser.getName() != null) {
                tvName.setText(mUser.getName());
            }
            if (mUser.getProfilePic() != null) {
                Glide.with(getActivity()).load(mUser.getProfilePic())
                        .circleCrop()
                        .into(imgUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}