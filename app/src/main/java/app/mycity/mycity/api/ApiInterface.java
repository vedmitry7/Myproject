package app.mycity.mycity.api;

import com.google.gson.JsonObject;

import app.mycity.mycity.api.model.DialogsContainer;
import app.mycity.mycity.api.model.ResponseAddComment;
import app.mycity.mycity.api.model.ResponseComments;
import app.mycity.mycity.api.model.ResponseDeleteComment;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.ResponseAuth;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseMarkAsRead;
import app.mycity.mycity.api.model.ResponsePostPhoto;
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseSocketServer;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.api.model.SendMessageResponse;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("auth.authorize")
    Call<JsonObject> auth(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("auth.authorize")
    Call<ResponseContainer<ResponseAuth>> authorize(@Field("email") String email,
                                                    @Field("password") String password);


    // get users

    @FormUrlEncoded
    @POST("users.get")
    Call<ResponseContainer<User>> getUser(@Field("access_token") String accessToken,
                                          @Field("fields") String fields);

    @FormUrlEncoded
    @POST("users.get")
    Call<ResponseContainer<User>> getUserById(@Field("access_token") String accessToken,
                                              @Field("user_id") String id,
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
    Call<ResponseContainer<UsersContainer>> getUsersById(@Field("access_token") String accessToken,
                                                         @Field("user_id") String id,
                                                         @Field("fields") String fields);

    @FormUrlEncoded
    @POST("friends.getOnline")
    Call<ResponseContainer<UsersContainer>> getUsersOnline(@Field("access_token") String accessToken);

    @FormUrlEncoded
    @POST("friends.getOnline")
    Call<ResponseContainer<UsersContainer>> getUsersOnlineWithFields(@Field("access_token") String accessToken,
                                                                     @Field("fields") String fields);

    @FormUrlEncoded
    @POST("friends.getOnline")
    Call<ResponseContainer<UsersContainer>> getUsersOnlineById(@Field("access_token") String accessToken,
                                                               @Field("user_id") String id,
                                                               @Field("fields") String fields);

    @FormUrlEncoded
    @POST("friends.add")
    Call<ResponseContainer<UsersContainer>> addToFriends(@Field("access_token") String accessToken,
                                                         @Field("user_id") String id);

    @FormUrlEncoded
    @POST("photos.getAll")
    Call<ResponseContainer<PhotoContainer>> getPhotosById(@Field("access_token") String accessToken,
                                                          @Field("owner_id") String id,
                                                          @Field("album_id") String albumId);

    @FormUrlEncoded
    @POST("users.search")
    Call<ResponseContainer<UsersContainer>> getFriendsList(@Field("access_token") String accessToken,
                                                           @Field("offset") int count);


    @FormUrlEncoded
    @POST("messages.getDialogs")
    Call<ResponseContainer<DialogsContainer>> getDialogs(@Field("access_token") String accessToken,
                                                         @Field("offset") int count);


    //send message
    @FormUrlEncoded
    @POST("messages.send")
    Call<ResponseContainer<SendMessageResponse>> sendMessage(@Field("access_token") String accessToken,
                                                             @Field("peer_id") long user_id,
                                                             @Field("chat_id") long chat_id,
                                                             @Field("text") String text);


    @FormUrlEncoded
    @POST("messages.markAsRead")
    Call<ResponseContainer<ResponseMarkAsRead>> markAsRead(@Field("access_token") String accessToken,
                                                           @Field("peer_id") long user_id);

    @FormUrlEncoded
    @POST("messages.markAsRead")
    Call<ResponseContainer<SendMessageResponse>> markAsReadMessages(@Field("access_token") String accessToken,
                                                            @Field("message_ids") long message_ids);

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


    //UPLOADING

    @FormUrlEncoded
    @POST("photos.getUploadServer")
    Call<ResponseContainer<ResponseUploadServer>> getUploadServer(@Field("access_token") String accessToken);

    @Multipart
    @POST("upload.php")
    Call<ResponseContainer<ResponseUploading>> upload( @Part("action") RequestBody action,
                                                       @Part("user_id") RequestBody userId,
                                                       @Part MultipartBody.Part filePart);

    //save like avatar
    @FormUrlEncoded
    @POST("photos.saveUserPhoto")
    Call<ResponseContainer<ResponseSavePhoto>> savePhoto(@Field("access_token") String accessToken,
                                                         @Field("photo_list") String photoList,
                                                         @Field("server") String server);

    //just save
    @FormUrlEncoded
    @POST("photos.save")
    Call<ResponseContainer<ResponseSavePhoto>> savePost(@Field("access_token") String accessToken,
                                                         @Field("photo_list") String photoList,
                                                         @Field("album_id") String albumId,
                                                         @Field("server") String server);

    @Multipart
    @POST("photos.saveUserPhoto")
    Call<ResponseContainer<ResponseSavePhoto>> savePhoto2(@Part("access_token") RequestBody action,
                                                          @Part MultipartBody.Part filePart);


    @Multipart
    @POST("wall.post")
    Call<ResponseContainer<ResponsePostPhoto>> postPicture(@Part("access_token") RequestBody action,
                                                           @Part("place_id") RequestBody placeId,
                                                           @Part("message") RequestBody message,
                                                           @Part("latitude") RequestBody latitude,
                                                           @Part("longitude") RequestBody longitude,
                                                           @Part("attachments") RequestBody attachments);


    @FormUrlEncoded
    @POST("wall.get")
    Call<ResponseContainer<ResponseWall>> getWallById(@Field("access_token") String token,
                                                  @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("wall.get")
    Call<ResponseContainer<ResponseWall>> getWall(@Field("access_token") String token);



    @FormUrlEncoded
    @POST("likes.add")
    Call<ResponseContainer<ResponseLike>> like(@Field("access_token") String token,
                                               @Field("type") String type,
                                               @Field("item_id") String itemId,
                                               @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("likes.delete")
    Call<ResponseContainer<ResponseLike>> unlike(@Field("access_token") String token,
                                               @Field("type") String type,
                                               @Field("item_id") String itemId,
                                               @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("feed.get")
    Call<ResponseContainer<ResponseWall>> getFeed(@Field("access_token") String token,
                                                 @Field("extended") String type,
                                                 @Field("offset") int offset,
                                                 @Field("fields") String fields);


    @FormUrlEncoded
    @POST("wall.getComments")
    Call<ResponseContainer<ResponseComments>> getComment(@Field("access_token") String token,
                                                         @Field("sort") String sort,
                                                         @Field("post_id") String postId,
                                                         @Field("owner_id") String ownerId,
                                                         @Field("offset") int offset,
                                                         @Field("extended") String extended,
                                                         @Field("count") int count,
                                                         @Field("fields") String fields);

    //return comment id
    @FormUrlEncoded
    @POST("wall.createComment")
    Call<ResponseContainer<ResponseAddComment>> addComment(@Field("access_token") String token,
                                                           @Field("post_id") String postId,
                                                           @Field("owner_id") String ownerId,
                                                           @Field("text") String text);

    @FormUrlEncoded
    @POST("wall.deleteComment")
    Call<ResponseContainer<ResponseDeleteComment>> deleteComment(@Field("access_token") String token,
                                                                 @Field("comment_id") String postId,
                                                                 @Field("owner_id") String ownerId);

    @GET("getSocketServer")
    Call<ResponseContainer<ResponseSocketServer>> getSocketServer(@Field("access_token") String token);

}
