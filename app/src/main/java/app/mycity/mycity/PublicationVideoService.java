package app.mycity.mycity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePostPhoto;
import app.mycity.mycity.api.model.ResponseSaveVideo;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploadingVideo;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static okhttp3.RequestBody.create;


public class PublicationVideoService extends Service {

    String path;
    File file;

    long startTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Test2", "PublicationService onBind ");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TAG23", "Service: onStartCommand. Intent - " + intent.getStringExtra("path"));
        path = intent.getStringExtra("path");
        file = new File(path);
        post();
        return START_NOT_STICKY;
    }

    public void post(){
        getUploadServer();
    }

    void getUploadServer(){
        startTime = System.currentTimeMillis();
        Log.d("TAG23","get server " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));

        ApiFactory.getApi().getUploadVideoServer(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseUploadServer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseUploadServer>> call, Response<ResponseContainer<ResponseUploadServer>> response) {
                ResponseUploadServer uploadServer = response.body().getResponse();
                Log.d("TAG23", "uploadServer" + uploadServer.getServer());
                uploadFile(uploadServer.getBaseUrl());
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseUploadServer>> call, Throwable t) {
                Log.d("TAG23", "failure - " + t.getLocalizedMessage());
                Log.d("TAG23", "failure - " + t.getCause());
                EventBus.getDefault().postSticky(new EventBusMessages.PublicationError());
            }
        });
    }

    private void uploadFile(String server) {
        Log.d("TAG23","getUploadPhotoServer - " + (startTime - System.currentTimeMillis()));
        startTime = System.currentTimeMillis();
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("0", file.getName(), RequestBody.create(MediaType.parse("video/mp4"), file));
        final RequestBody action = RequestBody.create(MediaType.parse("text/plain"), "add_video");
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), SharedManager.getProperty(Constants.KEY_MY_ID));
        Log.d("TAG23","create multipart - " + (startTime - System.currentTimeMillis()));
        startTime = System.currentTimeMillis();
        ApiFactory.getmApiUploadServer(server).uploadVideo(action, id, filePart).enqueue(new Callback<ResponseContainer<ResponseUploadingVideo>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseUploadingVideo>> call, Response<ResponseContainer<ResponseUploadingVideo>> response) {
                Log.d("TAG23","response  - " + (startTime - System.currentTimeMillis()));
                startTime = System.currentTimeMillis();
                ResponseUploadingVideo uploading = response.body().getResponse();
                JSONArray array = new JSONArray();
                for (int i = 0; i < uploading.getVideoList().size(); i++) {
                    try {
                        array.put(i, uploading.getVideoList().get(i).getJson());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                saveVideo(array.toString(), uploading.getServer());
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseUploadingVideo>> call, Throwable t) {
                Log.d("TAG21", "fail " + t.getCause());
                EventBus.getDefault().postSticky(new EventBusMessages.PublicationError());
            }
        });
    }

    void saveVideo(String string, String server){
        Log.d("TAG23","uploadFile - " + (startTime - System.currentTimeMillis()));
        startTime = System.currentTimeMillis();

        ApiFactory.getApi().saveVideo(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), string, server)
                .enqueue(new Callback<ResponseContainer<ResponseSaveVideo>>() {
                    @Override
                    public void onResponse(Call<ResponseContainer<ResponseSaveVideo>> call, Response<ResponseContainer<ResponseSaveVideo>> response) {

                        ResponseSaveVideo saveVideo = response.body().getResponse();
                        Log.d("TAG21", "success - " + saveVideo.getSuccess());

                        String attachment = "video" + SharedManager.getProperty(Constants.KEY_MY_ID) + "_" + saveVideo.getVideoId();

                        postVideo(attachment);
                    }

                    @Override
                    public void onFailure(Call<ResponseContainer<ResponseSaveVideo>> call, Throwable t) {
                        Log.d("TAG21","save fail");
                        EventBus.getDefault().postSticky(new EventBusMessages.PublicationError());
                    }
                });
    }



    private void postVideo(String attachment) {

        Log.d("TAG23","save post - " + (startTime - System.currentTimeMillis()));
        startTime = System.currentTimeMillis();

        Log.d("TAG21", "Registration " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN) +" "+ attachment);

        RequestBody token = create(
                MediaType.parse("text/plain"),
                SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));

        RequestBody placeId = create(
                MediaType.parse("text/plain"), SharedManager.getProperty("currentPlace"));
        RequestBody message = create(
                MediaType.parse("text/plain"), "");
        RequestBody att = create(
                MediaType.parse("text/plain"), attachment);

        final MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("attachments", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

        Log.d("TAG21", "api - ");

        try {
            ApiFactory.getApi().postPicture(token, placeId, message, att).enqueue(new Callback<ResponseContainer<ResponsePostPhoto>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponsePostPhoto>> call, Response<ResponseContainer<ResponsePostPhoto>> response) {
                    Log.d("TAG21", "response - ");

                    Log.d("TAG21", "resp != null " + String.valueOf(response != null));
                    //   Log.d("TAG21", "resp resp != null " + String.valueOf(response.body().getResponse() != null));

                    ResponsePostPhoto  responsePostPhoto = response.body().getResponse();
                    Log.d("TAG21", "Post id - " + responsePostPhoto.getPostId());

                    Log.d("Test2", "Post id - " + responsePostPhoto.getPostId() + " publication");

                    Log.d("TAG23","post picture - " + (startTime - System.currentTimeMillis()));


                    EventBus.getDefault().postSticky(new EventBusMessages.PublicationComplete());
                    //         progressDialog.hide();
                //    finish();
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponsePostPhoto>> call, Throwable t) {
                    Log.d("TAG21", "fail ");
                    EventBus.getDefault().postSticky(new EventBusMessages.PublicationError());

                }
            });
        } catch (Exception e){
            Log.d("TAG21", "e " + e.getLocalizedMessage());
            Log.d("TAG21", "e " + e.getCause());

        }
    }

/*    private void generateNotification() {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_main)
                        .setContentTitle("Чекин не был опубликован")
                        .setContentText("из-за ошибки");

        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        long[] vibrate = { 0, 200, 100, 100, 100, 50};
        mBuilder.setVibrate(vibrate);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(3456443, mBuilder.build());
    }*/

}
