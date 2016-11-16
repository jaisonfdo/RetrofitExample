package droidmentor.retrofitexample;

import java.util.Map;

import Json_Model.UserDetails;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

/**
 * Created by Jaison
 */
public interface ApiInterface
{
    @GET("user")
    Call<UserDetails> getUser(@QueryMap Map<String, String> params);

    @POST("user")
    Call<UserDetails> postUser(@QueryMap Map<String, String> params);

    @PUT("user")
    Call<UserDetails> updateUser(@QueryMap Map<String, String> params);
}
