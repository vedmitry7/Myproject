package app.mycity.mycity.api;

import com.google.gson.JsonObject;

import app.mycity.mycity.api.model.ResponseUsers;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.api.model.ResponseAuth;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("auth.authorize")
    Call<JsonObject> auth(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth.authorize")
    Call<ResponseContainer<ResponseAuth>> authorize(@Field("email") String email,
                                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("users.get")
    Call<ResponseContainer<User>> getUser(@Field("access_token") String accessToken,
                                          @Field("fields") String fields);

    @FormUrlEncoded
    @POST("friends.get")
    Call<ResponseContainer<UsersContainer>> getUsers(@Field("access_token") String accessToken);

    @FormUrlEncoded
    @POST("friends.get")
    Call<ResponseContainer<UsersContainer>> getUsersWithFields(@Field("access_token") String accessToken,
                                                     @Field("fields") String fields);

    @FormUrlEncoded
    @POST("friends.get")
    Call<ResponseContainer<UsersContainer>> getUsers(@Field("access_token") String accessToken,
                                                     @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("friends.get")
    Call<ResponseUsers> getUsers2(@Field("access_token") String accessToken);

/*    @FormUrlEncoded
    @POST("auth.signUp")
    Call<FullResponse> singUp(@Field("first_name") String firstName,
                                    @Field("secondName") String secondName,
                                    @Field("birthday") String birthday,
                                    @Field("email") String email,
                                    @Field("sex") int sex);

    @FormUrlEncoded
    @POST("auth.checkCode")
    Call<FullResponse> checkCode(@Field("email") String email,
                                 @Field("code") int code);

    @FormUrlEncoded
    @POST("auth.confirm")
    Call<FullResponse> confirm(@Field("email") String email,
                               @Field("code") int code,
                               @Field("password") String password,
                               @Field("confirm_password") String confirmPassword,
                               @Field("intro") boolean intro);*/
}
