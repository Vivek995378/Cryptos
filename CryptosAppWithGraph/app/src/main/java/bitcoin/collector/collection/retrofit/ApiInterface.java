package bitcoin.collector.collection.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("price")
    Call<Object> getData(@Query("fsym") String fsym, @Query("tsyms") String tsyms);

    /*compare*/
    @GET("pricemulti")
    Call<Object> compareCrypto(@Query("fsyms") String from, @Query("tsyms") String to);
}
