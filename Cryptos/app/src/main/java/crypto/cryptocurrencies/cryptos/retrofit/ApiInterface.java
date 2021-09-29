package crypto.cryptocurrencies.cryptos.retrofit;

import crypto.cryptocurrencies.cryptos.models.price.PriceHistPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    /*get currency price*/
    @GET("histoday")
    Call<PriceHistPojo> getCurrencyPriceList(@Query("fsym") String fsym, @Query("tsym") String tsyms,
                                             @Query("limit") String limit);

    /*compare*/
    @GET("pricemulti")
    Call<Object> compareCrypto(@Query("fsyms") String from, @Query("tsyms") String to);
}
