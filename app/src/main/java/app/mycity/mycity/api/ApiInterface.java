package app.mycity.mycity.api;

import com.google.gson.JsonObject;

import app.mycity.mycity.api.model.CheckTokenResponse;
import app.mycity.mycity.api.model.DialogsContainer;
import app.mycity.mycity.api.model.MessageResponse;
import app.mycity.mycity.api.model.NotificationResponce;
import app.mycity.mycity.api.model.PlaceCategoryResponce;
import app.mycity.mycity.api.model.PlacesResponse;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.RefreshTokenResponse;
import app.mycity.mycity.api.model.ResponseAddComment;
import app.mycity.mycity.api.model.ResponseAlbums;
import app.mycity.mycity.api.model.ResponseComments;
import app.mycity.mycity.api.model.ResponseDeleteComment;
import app.mycity.mycity.api.model.ResponseEventVisitors;
import app.mycity.mycity.api.model.ResponseEvents;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.ResponseAuth;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseMarkAsRead;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.api.model.ResponsePostPhoto;
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseSocketServer;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.api.model.ResponseVisit;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.api.model.SendMessageResponse;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.api.model.SuccessResponceNumber;
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


    //check token
    @FormUrlEncoded
    @POST("auth.authentication")
    Call<ResponseContainer<CheckTokenResponse>> checkToken(@Field("access_token") String accessToken);

    //update token
    @FormUrlEncoded
    @POST("auth.refresh")
    Call<ResponseContainer<RefreshTokenResponse>> updateToken(@Field("access_token") String accessToken,
                                                              @Field("refresh_token") String refreshToken);

    // get users

    @FormUrlEncoded
    @POST("users.get")
    Call<ResponseContainer<Profile>> getUser(@Field("access_token") String accessToken,
                                             @Field("fields") String fields);

    //TOP users
    @FormUrlEncoded
    @POST("users.getAll")
    Call<ResponseContainer<UsersContainer>> getTopUsers(@Field("access_token") String accessToken,
                                                        @Field("fields") String fields,
                                                        @Field("order") String order);

    @FormUrlEncoded
    @POST("users.getAll")
    Call<ResponseContainer<UsersContainer>> getTopUsersWithSearch(@Field("access_token") String accessToken,
                                                        @Field("fields") String fields,
                                                        @Field("order") String order,
                                                        @Field("filter") String filter,
                                                        @Field("q") String search);


    //TOP users
    @FormUrlEncoded
    @POST("users.getAll")
    Call<ResponseContainer<UsersContainer>> getTopUsersInPlaces(@Field("access_token") String accessToken,
                                                        @Field("fields") String fields,
                                                        @Field("order") String order,
                                                        @Field("filter") String filter); //in_place

    @FormUrlEncoded
    @POST("users.getAll")
    Call<ResponseContainer<UsersContainer>> getTopUsersInPlacesWithSorting(@Field("access_token") String accessToken,
                                                                @Field("fields") String fields,
                                                                @Field("order") String order,
                                                                @Field("filter") String filter, //in_place
                                                                @Field("sex") int sex,
                                                                @Field("age_from") int from,
                                                                @Field("age_to") int to,
                                                                           @Field("extended") String extended);

    //Subscribers
    @FormUrlEncoded
    @POST("subscribers.get")
    Call<ResponseContainer<UsersContainer>> getSubscribers(@Field("access_token") String accessToken,
                                                           @Field("user_id") String id,
                                                           @Field("only_online") int online,
                                                           @Field("fields") String fields);

    //Subscribers Count
    @FormUrlEncoded
    @POST("subscribers.get")
    Call<ResponseContainer<UsersContainer>> getSubscribersCount(@Field("access_token") String accessToken);

    //Subscriptions // 1 - online
    @FormUrlEncoded
    @POST("subscribers.getSubscriptions")
    Call<ResponseContainer<UsersContainer>> getSubscriptions(@Field("access_token") String accessToken,
                                                             @Field("user_id") String id,
                                                             @Field("only_online") int online,
                                                             @Field("fields") String fields);

    @FormUrlEncoded
    @POST("subscribers.getSubscriptions")
    Call<ResponseContainer<UsersContainer>> getSubscriptionsCount(@Field("access_token") String accessToken);


    @FormUrlEncoded
    @POST("users.get")
    Call<ResponseContainer<Profile>> getUserById(@Field("access_token") String accessToken,
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
    @POST("subscribers.add")
    Call<ResponseContainer<Success>> addSubscription(@Field("access_token") String accessToken,
                                                     @Field("user_id") String id);

    @FormUrlEncoded
    @POST("subscribers.delete")
    Call<ResponseContainer<Success>> deleteSubscription(@Field("access_token") String accessToken,
                                                         @Field("user_id") String id);

    @FormUrlEncoded
    @POST("users.search")
    Call<ResponseContainer<UsersContainer>> getFriendsList(@Field("access_token") String accessToken,
                                                           @Field("offset") int count);


    @FormUrlEncoded
    @POST("messages.getDialogs")
    Call<ResponseContainer<DialogsContainer>> getDialogs(@Field("access_token") String accessToken,
                                                         @Field("offset") int count);


    @FormUrlEncoded
    @POST("messages.deleteDialog")
    Call<ResponseContainer<SuccessResponceNumber>> deleteDialogs(@Field("access_token") String accessToken,
                                                         @Field("peer_id") String id);

    //send message
    @FormUrlEncoded
    @POST("messages.send")
    Call<ResponseContainer<SendMessageResponse>> sendMessage(@Field("access_token") String accessToken,
                                                             @Field("peer_id") String user_id,
                                                             @Field("chat_id") long chat_id,
                                                             @Field("text") String text);


    @FormUrlEncoded
    @POST("messages.markAsRead")
    Call<ResponseContainer<SuccessResponceNumber>> markAsRead(@Field("access_token") String accessToken,
                                                           @Field("peer_id") String user_id);

    @FormUrlEncoded
    @POST("messages.markAsRead")
    Call<ResponseContainer<SuccessResponceNumber>> markAsReadMessages(@Field("access_token") String accessToken,
                                                                    @Field("message_ids") long message_ids);


    @FormUrlEncoded
    @POST("messages.delete")
    Call<ResponseContainer<SuccessResponceNumber>> deleteMessages(@Field("access_token") String accessToken,
                                                                  @Field("message_ids") long message_ids);

    @FormUrlEncoded
    @POST("messages.getHistory")
    Call<ResponseContainer<MessageResponse>> getMessages(@Field("access_token") String accessToken,
                                                         @Field("peer_id") String peer_id,
                                                         @Field("offset") int offset);
    @FormUrlEncoded
    @POST("messages.getHistory")
    Call<ResponseContainer<SendMessageResponse>> getMessagesFromId(@Field("access_token") String accessToken,
                                                                    @Field("peer_id") long peerId,
                                                                    @Field("start_message_id") long startMessageId,
                                                                    @Field("offset") int offset);



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

    @FormUrlEncoded
    @POST("photos.getUploadServerAvatar")
    Call<ResponseContainer<ResponseUploadServer>> getUploadServerAvatar(@Field("access_token") String accessToken);

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


    @FormUrlEncoded
    @POST("photos.getAlbums")
    Call<ResponseContainer<ResponseAlbums>> getGroupAlbums(@Field("access_token") String accessToken,
                                                           @Field("group_id") String groupId,
                                                           @Field("offset") int offset);

    @FormUrlEncoded
    @POST("photos.getAlbums")
    Call<ResponseContainer<ResponseAlbums>> getAllGroupAlbums(@Field("access_token") String accessToken,
                                                           @Field("offset") int offset,
                                                              @Field("extended") String extended,
                                                              @Field("only_subscription") String subscriptionOnly);

    @FormUrlEncoded
    @POST("photos.getAll")
    Call<ResponseContainer<PhotoContainer>> getAlbum(@Field("access_token") String accessToken,
                                                     @Field("group_id") String groupId,
                                                     @Field("album_id") String albumId,
                                                     @Field("extended") String extended);


    @Multipart
    @POST("wall.post")
    Call<ResponseContainer<ResponsePostPhoto>> postPicture(@Part("access_token") RequestBody action,
                                                           @Part("place_id") RequestBody placeId,
                                                           @Part("message") RequestBody message,
                                                           @Part("attachments") RequestBody attachments);


    @FormUrlEncoded
    @POST("wall.get")
    Call<ResponseContainer<ResponseWall>> getWallById(@Field("access_token") String token,
                                                      @Field("owner_id") String ownerId,
                                                      @Field("extended") String extended);
    @FormUrlEncoded
    @POST("wall.get")
    Call<ResponseContainer<ResponseWall>> getGroupWallById(@Field("access_token") String token,
                                                           @Field("place_id") String placeId,
                                                           @Field("filter") String filters,
                                                           @Field("extended") String extended,
                                                           @Field("fields") String fields,   //for users
                                                           @Field("offset") int offset);

    @FormUrlEncoded
    @POST("wall.get")
    Call<ResponseContainer<ResponseWall>> getWall(@Field("access_token") String token,
                                                  @Field("offset") int offset,
                                                  @Field("count") int count);


    @FormUrlEncoded
    @POST("wall.get")
    Call<ResponseContainer<ResponseWall>> getWallExtended(@Field("access_token") String token,
                                                  @Field("offset") int offset,
                                                  @Field("count") int count,
                                                  @Field("extended") String extended);




    @FormUrlEncoded
    @POST("likes.add")
    Call<ResponseContainer<ResponseLike>> like(@Field("access_token") String token,
                                               @Field("type") String type,//post,photo
                                               @Field("item_id") String itemId,
                                               @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("likes.delete")
    Call<ResponseContainer<ResponseLike>> unlike(@Field("access_token") String token,
                                                 @Field("type") String type,
                                                 @Field("item_id") String itemId,
                                                 @Field("owner_id") String ownerId);


    //PLACE EVENTS
    @FormUrlEncoded
    @POST("events.get")
    Call<ResponseContainer<ResponseWall>> getEvents(@Field("access_token") String token,
                                                    @Field("group_id") String groupId,
                                                    @Field("extended") String type,
                                                    @Field("offset") int offset);


    @FormUrlEncoded
    @POST("events.getById")
    Call<ResponseContainer<ResponseEvents>> getEventsById(@Field("access_token") String token,
                                                          @Field("events") String events);


    //PLACE EVENTS
    @FormUrlEncoded
    @POST("events.getAll")
    Call<ResponseContainer<ResponseWall>> getAllEvents(@Field("access_token") String token,
                                                       @Field("extended") String type,
                                                       @Field("offset") int offset);

    @FormUrlEncoded
    @POST("events.addVisit")
    Call<ResponseContainer<ResponseVisit>> addVisit(@Field("access_token") String token,
                                                    @Field("event_id") String eventId,
                                                    @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("events.removeVisit")
    Call<ResponseContainer<ResponseVisit>> removeVisit(@Field("access_token") String token,
                                                       @Field("event_id") String eventId,
                                                       @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("events.getVisitors")
    Call<ResponseContainer<ResponseEventVisitors>> getVisitors(@Field("access_token") String token,
                                                                     @Field("event_id") String eventId,
                                                                     @Field("owner_id") String ownerId,
                                                               @Field("fields") String fields);


    @FormUrlEncoded
    @POST("actions.getAll")
    Call<ResponseContainer<ResponseWall>> getAllActions(@Field("access_token") String token,
                                                       @Field("extended") String type,
                                                       @Field("offset") int offset);

    @FormUrlEncoded
    @POST("actions.getAll")
    Call<ResponseContainer<ResponseWall>> getAllActionsByGroupId(@Field("access_token") String token,
                                                        @Field("extended") String type,
                                                        @Field("offset") int offset,
                                                        @Field("owner_id") String ownerId);

    //FEED
    @FormUrlEncoded
    @POST("feed.get")
    Call<ResponseContainer<ResponseWall>> getFeed(@Field("access_token") String token,
                                                  @Field("extended") String type,
                                                  @Field("offset") int offset,
                                                  @Field("fields") String fields,
                                                  @Field("filter") String filter);


    //FEED
    @FormUrlEncoded
    @POST("feed.get")
    Call<ResponseContainer<ResponseWall>> getTopCheckin(@Field("access_token") String token,
                                                        @Field("extended") String type,
                                                        @Field("offset") int offset,
                                                        @Field("fields") String fields,
                                                        @Field("order") String order);  // top, popular


    @FormUrlEncoded
    @POST("wall.getComments")
    Call<ResponseContainer<ResponseComments>> getCommentsPost(@Field("access_token") String token,
                                                              @Field("sort") String sort,
                                                              @Field("post_id") String postId,
                                                              @Field("owner_id") String ownerId,
                                                              @Field("offset") int offset,
                                                              @Field("extended") String extended,
                                                              @Field("count") int count,
                                                              @Field("fields") String fields);


    @FormUrlEncoded
    @POST("photos.getComments")
    Call<ResponseContainer<ResponseComments>> getCommentsPhoto(@Field("access_token") String token,
                                                               @Field("sort") String sort,
                                                               @Field("photo_id") String postId,
                                                               @Field("owner_id") String ownerId,
                                                               @Field("offset") int offset,
                                                               @Field("extended") String extended,
                                                               @Field("count") int count,
                                                               @Field("fields") String fields);

    @FormUrlEncoded
    @POST("events.getComments")
    Call<ResponseContainer<ResponseComments>> getCommentsEvent(@Field("access_token") String token,
                                                               @Field("sort") String sort,
                                                               @Field("event_id") String eventId,
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

    //return comment id
    @FormUrlEncoded
    @POST("photos.createComment")
    Call<ResponseContainer<ResponseAddComment>> addCommentPhoto(@Field("access_token") String token,
                                                           @Field("photo_id") String postId,
                                                           @Field("owner_id") String ownerId,
                                                           @Field("text") String text);

    @FormUrlEncoded
    @POST("events.createComment")
    Call<ResponseContainer<ResponseAddComment>> addCommentEvent(@Field("access_token") String token,
                                                           @Field("event_id") String postId,
                                                           @Field("owner_id") String ownerId,
                                                           @Field("text") String text);

    @FormUrlEncoded
    @POST("wall.deleteComment")
    Call<ResponseContainer<ResponseDeleteComment>> deleteComment(@Field("access_token") String token,
                                                                 @Field("comment_id") String postId,
                                                                 @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("photos.deleteComment")
    Call<ResponseContainer<ResponseDeleteComment>> deleteCommentPhoto(@Field("access_token") String token,
                                                                 @Field("comment_id") String postId,
                                                                 @Field("owner_id") String ownerId);

    @FormUrlEncoded
    @POST("messages.getSocketServer")
    Call<ResponseContainer<ResponseSocketServer>> getSocketServer(@Field("access_token") String token);

    @FormUrlEncoded
    @POST("groups.getAll")
    Call<ResponseContainer<ResponsePlaces>> getPlaces(@Field("access_token") String token,
                                                      @Field("offset") int offset,
                                                      @Field("city_id ") int cityId,
                                                      @Field("category_id") int category,
                                                      @Field("order") String order,
                                                      @Field("q") String filter);

    @FormUrlEncoded
    @POST("groups.get")
    Call<ResponseContainer<ResponsePlaces>> getPlacesByUserId(@Field("access_token") String token,
                                                      @Field("offset") int offset,
                                                      @Field("city_id ") int cityId,
                                                      @Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("groups.getbyId")
    Call<PlacesResponse> getPlaceByIds(@Field("access_token") String token,
                                       @Field("group_ids") String groupIds);

    @FormUrlEncoded
    @POST("groups.getMembers")
    Call<ResponseContainer<UsersContainer>> getPlaceSubscribers(@Field("access_token") String token,
                                                                @Field("group_id") String groupIds,
                                                                @Field("fields") String fields,
                                                                @Field("subscriptions_only") String subscriptions);

    @FormUrlEncoded
    @POST("groups.getMembersinplace")
    Call<ResponseContainer<UsersContainer>> getUsersInPlace(@Field("access_token") String token,
                                                                @Field("group_id") String groupIds,
                                                                @Field("fields") String fields,
                                                                @Field("subscriptions_only") String subscriptions);

    @FormUrlEncoded
    @POST("groups.join")
    Call<ResponseContainer<Success>> joinToGroup(@Field("access_token") String token,
                                                            @Field("group_id") String groupIds);

    @FormUrlEncoded
    @POST("groups.leave")
    Call<ResponseContainer<Success>>  leaveGroup(@Field("access_token") String token,
                                                        @Field("group_id") String groupIds);

    @FormUrlEncoded
    @POST("groups.getNearby")
    Call<ResponseContainer<ResponsePlaces>> getPlaceByCoordinates(@Field("access_token") String token,
                                                                  @Field("latitude") String latitude,
                                                                  @Field("longitude") String longitude,
                                                                  @Field("radius") int radius);

    @FormUrlEncoded
    @POST("account.setCoordinates")
    Call<ResponseContainer<UsersContainer>> setCoordinates(@Field("access_token") String token,
                                                            @Field("latitude") Double latitude,
                                                            @Field("longitude") Double longitude);

    @FormUrlEncoded
    @POST("database.getCategories")
    Call<PlaceCategoryResponce> getPlaceCategories(@Field("access_token") String token);

    @FormUrlEncoded
    @POST("notifications.get")
    Call<ResponseContainer<NotificationResponce>> getNotifications(@Field("access_token") String token,
                                                                   @Field("offset") int offset);


    @FormUrlEncoded
    @POST("groups.rate")
    Call<ResponseContainer<Success>> rateGroup(@Field("access_token") String token,
                                                @Field("group_id") String groupIds,
                                                @Field("service") String service
                                              /* ,
                                                @Field("quality") String quality,
                                                @Field("price") String price,
                                                @Field("interior") String interior*/
    );

    @FormUrlEncoded
    @POST("groups.rate")
    Call<ResponseContainer<Success>> rate(@Field("access_token") String token,
                                                @Field("group_id") String groupIds,
                                                @Field("type") String service,
                                                @Field("rate") int rate);


}
