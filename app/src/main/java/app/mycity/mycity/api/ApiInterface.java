package app.mycity.mycity.api;

import app.mycity.mycity.api.model.FullResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("auth.authorize")
    Call<FullResponse> auth(@Field("email") String email, @Field("password") String password);
}
