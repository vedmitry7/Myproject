package app.mycity.mycity.api;


import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface ApiRetrofitInterface {

    @FormUrlEncoded
    @POST("users.get")
    Observable<ResponseContainer<Profile>> getUser(@Field("access_token") String accessToken,
                                                   @Field("fields") String fields);
}
