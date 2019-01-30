package app.mycity.mycity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.CheckTokenResponse;
import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.api.model.RealmUser;
import app.mycity.mycity.api.model.RefreshTokenResponse;
import app.mycity.mycity.api.model.ResponseAuth;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseSocketServer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.ChatActivity;
import app.mycity.mycity.views.activities.MainActivity3;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocketService extends Service {

    private Socket mSocket;

    Realm mRealm;

    private int second;

    TimerTask timerTask;

    Timer timer;

    private void updateToken(){
        ApiFactory.getApi().updateToken(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), SharedManager.getProperty(Constants.KEY_REFRESH_TOKEN)).enqueue(new Callback<ResponseContainer<RefreshTokenResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<RefreshTokenResponse>> call, Response<ResponseContainer<RefreshTokenResponse>> response) {

                RefreshTokenResponse tokenResponse = response.body().getResponse();

                if(tokenResponse!=null){
                    Log.i("Test", "new Token!");
                    SharedManager.addProperty(Constants.KEY_ACCESS_TOKEN, tokenResponse.getAccessToken());
                    SharedManager.addProperty(Constants.KEY_REFRESH_TOKEN, tokenResponse.getRefreshToken());
                    SharedManager.addProperty(Constants.KEY_EXPIRED_AT, String.valueOf(tokenResponse.getExpiredAt()));
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<RefreshTokenResponse>> call, Throwable t) {

            }
        });
    }

    public SocketService() {
        Log.i("Test", "Service: constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Test", "Service: onBind. Reason - " + intent.getStringExtra("data"));
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Test", "Service: onCreate");

        if(!SharedManager.getBooleanProperty("login")){
            Log.i("Test", "onCreate. Login false Service: STOP SELF");
            stopSelf();
            return;
        } else {
            Log.i("Test", "onCreate. Login true");
        }

        EventBus.getDefault().register(this);
        mRealm = Realm.getDefaultInstance();
        Intent ishintent = new Intent(this, SocketService.class);
        ishintent.putExtra("data", "alarm");
        PendingIntent pintent = PendingIntent.getService(this, 6, ishintent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //  alarm.cancel(pintent);
        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),560000, pintent);

        checkToken();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startSubscribers(EventBusMessages.UpdateSocketConnection event) {
        Log.i("TAG25", "socket hand reconnect");
        timerTask.cancel();
        mSocket.off("history");
        mSocket.disconnect();
        mSocket.close();
        initSocket();
    }

    private void initSocket() {
        Log.i("Test", "initSocket");
        Log.i("TAG25", "init socket");
        {
            try {
                if(SharedManager.getProperty("socketServer")!=null)
                    mSocket = IO.socket("http://" + SharedManager.getProperty("socketServer"));
                else
                    Log.i("TAG25", "server null");

                //   mSocket = IO.socket("http://192.168.0.104:8000");
            } catch (URISyntaxException e) {}
        }

        mSocket.connect();
        Log.i("Test", "Socket was connect");

        mSocket.off("history");

        JSONObject obj = new JSONObject();
        long l = 15350138831666L;
        //  SharedManager.addProperty("ts", String.valueOf(l));
        try {
            obj.put("hash", SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));
            if(SharedManager.getProperty("ts")==null){
                SharedManager.addProperty("ts", String.valueOf(l));
            }
            if(SharedManager.getProperty("ts")!=null){
                obj.put("ts", Long.parseLong(SharedManager.getProperty("ts")));
                Log.d("TAG21", "TS AUTH - " + SharedManager.getProperty("ts"));
            }
            else{
                obj.put("ts", l);
                Log.d("TAG21", "TS AUTH old - " + l);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("auth", obj);

        Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String history = "" + args[0];
                Log.d("TAG25", "History - " + args[0]);
                chatResponse2(history);
                //    EventBus.getDefault().post(new EventBusMessages.Message(history));
            }
        };

        mSocket.on("history", listener);

        final boolean[] wasLost = new boolean[1];

        Log.i("Test", "Socket create task");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("Test", this + " socket connected - " + mSocket.connected());

                if(!mSocket.connected()){
                    Log.d("Test", "socket lost connection");
                    wasLost[0] = true;
                } else {
                    if(wasLost[0]){
                        Log.d("Test", "socket get connection after lost");
                       wasLost[0] = false;
                        mSocket.off("history");
                        mSocket.disconnect();
                        mSocket.close();
                        this.cancel();
                        checkToken();
                    }
                }
            }
        };

        // check if socket lost connection and get it again. then init again, can be problem with
        timer = new Timer();
        timer.schedule(timerTask, 10000, 5000);
        Log.i("Test", "task was scheduled");

    }

    void chatResponse2(String responseString){


        Realm mRealm = Realm.getDefaultInstance();

        JSONObject jsonObject = null;
        JSONObject innerResponseObject = null;
        JSONArray jsonArray = null;

        try {
            jsonObject = new JSONObject(responseString);
            Log.i("TAG21", "jsonObj = " + String.valueOf(jsonObject!=null));

            jsonArray = jsonObject.getJSONArray("history");
            Log.i("TAG21", "jsonArray = " + String.valueOf(jsonArray!=null));
            Log.i("TAG21", "Size - " + String.valueOf(jsonArray.length()));

            String ts = jsonObject.getString("ts");
            Log.i("TAG21", "ts - " + ts);
            SharedManager.addProperty("ts", ts);

            int incoming = 1;
            String dialogId = "";
            String messageText = "";
            int unreadCount;
            int date = 0;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);

                String info = null;
                switch (array.getInt(0)){
                    case 1: info = "new message   "; break;
                    case 2: info = "was read      "; break;
                    case 5: info = "dialog update "; break;
                    case 6: info = "read dialog   "; break;
                    case 7: info = "общее кол-во непрочит. "; break;
                    case 21: info = "notification";
                }

                Log.i("TAG25", info + " " + array.length() + " : " + array.toString());


                int type = array.getInt(0);

                /*
                final long userId = array.getLong(2);
                final long time = array.getLong(1);

                int messageId = -1;
                if(array.length()>3){
                    messageId = array.getInt(4);
                }

*/
                Message message;
                Message messageToSend;

                switch (type){
                    case 1:
                        Log.i("TAG21", "new mes");
                        break;
                    case 2:
                        Log.i("TAG25", "execute was read");

                        int messageId = array.getInt(4);
                        EventBus.getDefault().post(new EventBusMessages.MessageWasRead(messageId));
                        break;
                    case 5:
                        //new message
                        final long userId = array.getLong(2);
                        final long time = array.getLong(1);
                        int mId = array.getInt(4);
                        final String text = array.getString(5);
                        final int out = array.getInt(6);
                        incoming = array.getInt(6);
                        messageText = array.getString(5);
                        dialogId = array.getString(2);
                        date = array.getInt(1);

                        Log.i("TAG25", "execute dialog update out - " + out);

                        message = new Message();
                        message.setId(mId);
                        message.setUser(userId);
                        message.setTime(time);
                        message.setText(text);
                        message.setOut(out);
                        message.setWasSended(true);
                        message.setWasRead(false);
                        EventBus.getDefault().post(new EventBusMessages.NewChatMessage(message, out));

                        if(out!=1){
                            if(((App) getApplicationContext()).isChatActivityStarted){
                                Log.i("TAG21", "chat started");
                                if(!((App) getApplicationContext()).getCurrentChatUser().equals(String.valueOf(userId))){
                                    Log.i("TAG21", "cur chat user - " + ((App) getApplicationContext()).getCurrentChatUser());

                                    generateNotificationNewMessage(text, String.valueOf(userId));
                                }
                            } else {
                                Log.i("TAG21", "chat not started");
                                generateNotificationNewMessage(text, String.valueOf(userId));
                            }

                        }
                        break;
                    case 6:
                        unreadCount = array.getInt(4);
                        if(incoming==0){
                            EventBus.getDefault().post(new EventBusMessages.DialogUpdate(dialogId, messageText, unreadCount, date));
                        }
                        break;
                    case 7:
                        Log.i("TAG25", "TOTAL UNREAD - " + array.getInt(2));
                        SharedManager.addIntProperty("totalUnreadCount", array.getInt(2));
                        EventBus.getDefault().postSticky(new EventBusMessages.UnreadCountUpdate());
                        break;
                    case 21:

                        String event = array.getString(2);
                        JSONObject obj = array.getJSONObject(3);
                        String name = obj.getString("first_name") + " " + obj.getString("last_name");
                        Log.i("TAG25", "NOTIFICATION - " + event + " " + name );

                        switch (event){
                            case "follow":{
                                generateNotification("На вас подписались", name);
                                break;
                            }
                            case "like_post":{
                                generateNotification("Ваш чекин оценили", name);
                                break;
                            }
                            case "like_comment":{
                                generateNotification("Ваш комментарий оценили", name);
                                break;
                            }
                            case "comment_post":{
                                generateNotification("Комментарий к чекину", name);
                                break;
                            }
                        }
                        break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG21", "JSON GET RESPONSE ERROR");
            Log.i("TAG25", "JSON GET RESPONSE ERROR");
        }
    }

    private void unreadCount(String userId){
        int num;
        if(SharedManager.getProperty("unread_"+ userId)!=null){
            num = Integer.parseInt(SharedManager.getProperty("unread_"+ userId));
        } else {
            SharedManager.addProperty("unread_" + userId, "0");
            num = 0;
        }
        SharedManager.addProperty("unread_" + userId, String.valueOf(++num));
    }

    private void generateNotification(String text, String userName) {


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_main)
                        .setContentTitle(text)
                        .setContentText(userName);
        //  mBuilder.setSound(alarmSound);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        long[] vibrate = { 0, 200, 100, 100, 100, 50};
        mBuilder.setVibrate(vibrate);

        Intent resultIntent = new Intent(this, MainActivity3.class);
        resultIntent.putExtra("data", "follow");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        //resultIntent.putExtra("user_id", userId);

        mBuilder.setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity3.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(25, mBuilder.build());

    }

    private void generateNotificationNewMessage(String text, String userId) {
        mRealm = Realm.getDefaultInstance();
        RealmUser user = mRealm.where(RealmUser.class).equalTo("id", userId).findFirst();

        String title;
        if(user!=null){
            title = user.getFirstName();
        } else {
            title = "Новое сообщение";
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_main)
                        .setContentTitle(title)
                        .setContentText(text);
        //  mBuilder.setSound(alarmSound);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        long[] vibrate = { 0, 200, 100, 100, 100, 50};
        mBuilder.setVibrate(vibrate);

        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        resultIntent.putExtra("user_id", userId);

        mBuilder.setAutoCancel(true);

        if(SharedManager.getProperty("unread_" + userId)!=null)
        mBuilder.setContentText(text).setNumber(Integer.parseInt(SharedManager.getProperty("unread_" + userId)));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Integer.parseInt(userId), mBuilder.build());

    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        PendingIntent test = PendingIntent.getService(this, 23, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
// error NullPointerException       Log.i("Test", "Service: onStartCommand. Intent - " + intent.getStringExtra("data") );

        if(!SharedManager.getBooleanProperty("login")){
            Log.i("Test", "onStartCommand. Login false Service: STOP SELF");
            stopSelf();
        } else {
            Log.i("Test", "onStartCommand. Login true");

            if(mSocket==null){
                Log.i("Test", "Socket is null");
                checkToken();
            } else {
                if(mSocket.connected()){
                    Log.i("Test", "Socket connected");
                } else {
                    Log.i("Test", "Socket not connected");
                }
            }
        }

        if(intent!=null){
            Log.i("Test", "onStartCommand - " + intent.getAction());
        } else {
            Log.i("Test", "onStartCommand - intent - null");
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {
        Log.i("Test", "Service: onDestroy");

        EventBus.getDefault().unregister(this);

        if(timerTask!=null){
            Log.i("Test", "Cancel timer task");
            timerTask.cancel();
            timer.cancel();
        } else {
            Log.i("Test", "Timer task = null");
        }
        if(mSocket!=null){
            mSocket.off("history");
            mSocket.disconnect();
            mSocket.close();
        }

        super.onDestroy();
        //  Log.i("Test", "Service: restart");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Test", "Service: onTaskRemoved. Reason - " + rootIntent.getStringExtra("data"));
    }


    private void checkToken() {
        Log.i("Test", "CheckToken");
        ApiFactory.getApi().checkToken(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<CheckTokenResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<CheckTokenResponse>> call, Response<ResponseContainer<CheckTokenResponse>> response) {
                if(response.body().getResponse()!=null){
                    if(response.body().getResponse().getSuccess()==1){
                        Log.i("Test", "CheckToken SUC");
                        initSocket();
                    }
                } else {
                    Log.i("Test", "CheckToken NOT SUCCESS");
                    authorize();
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<CheckTokenResponse>> call, Throwable t) {
                Log.i("Test", "CheckToken FAIL - " + t.getLocalizedMessage());
                App.appendLog("checkToken FAIL - " + t.getLocalizedMessage());
            }
        });
    }

    private void authorize() {
        Log.i("Test", "Authorize");
        ApiFactory.getApi().authorize(SharedManager.getProperty(Constants.KEY_LOGIN), SharedManager.getProperty(Constants.KEY_PASSWORD)).enqueue(new retrofit2.Callback<ResponseContainer<ResponseAuth>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponseAuth>> call, retrofit2.Response<ResponseContainer<ResponseAuth>> response) {
                ResponseAuth responseAuth = response.body().getResponse();
                if(responseAuth != null){
                    Log.i("TAG25", "USER ID - " + String.valueOf(responseAuth.getUserId()));
                    Log.i("TAG25", "TOKEN - " + responseAuth.getAccessToken());
                    Log.i("TAG25", "REFRESH_TOKEN - " + responseAuth.getRefreshToken());
                    Log.i("TAG25", "REFRESH_TOKEN - " + responseAuth.getRefreshToken());

                    SharedManager.addProperty(Constants.KEY_MY_ID, responseAuth.getUserId());
                    SharedManager.addProperty(Constants.KEY_ACCESS_TOKEN, responseAuth.getAccessToken());
                    SharedManager.addProperty(Constants.KEY_REFRESH_TOKEN, responseAuth.getRefreshToken());
                    SharedManager.addProperty(Constants.KEY_EXPIRED_AT, responseAuth.getExpiredAt());

                    Log.i("Test", "Authorize SUC");
                    initSocket();
         /*           new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {

                            //should check internet connection

                            getServerTime();

                        }
                    }, 0, 1000*60*60*23);*/

                } else {
                    Log.i("Test", "Authorize NOT Success");
                    Log.i("TAG", "error");

                    // some error
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponseAuth>> call, Throwable t) {
                Log.i("Test", "failure ");
                App.appendLog("Authorize FAIL - " + t.getLocalizedMessage());
                Toast.makeText(SocketService.this, "failure " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
