package bitcoin.collector.collection.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit AllCurrencyRetrofit;
    private static Retrofit compareCurrencyRetrofit;
    private static Retrofit bitcoinRetrofit;

    private static String v2Url = "https://min-api.cryptocompare.com/data/v2/";
    private static String simpleUrl = "https://min-api.cryptocompare.com/data/";

    public static Retrofit getAllCurrencyRetrofitInstance() {
        if (AllCurrencyRetrofit == null) {
            AllCurrencyRetrofit = new Retrofit.Builder()
                    .baseUrl(v2Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return AllCurrencyRetrofit;
    }

    public static Retrofit getCompareCurrencyRetrofitInstance() {
        if (compareCurrencyRetrofit == null) {
            compareCurrencyRetrofit = new Retrofit.Builder()
                    .baseUrl(simpleUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return compareCurrencyRetrofit;
    }

    public static Retrofit getBitcoinRetrofitInstance() {
        if (bitcoinRetrofit == null) {
            bitcoinRetrofit = new Retrofit.Builder()
                    .baseUrl("https://min-api.cryptocompare.com/data/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return bitcoinRetrofit;
    }
}
