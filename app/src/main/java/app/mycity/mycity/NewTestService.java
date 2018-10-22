package app.mycity.mycity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

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

import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.ChatActivity;
import io.realm.Realm;

public class NewTestService extends Service {

    private Socket mSocket;

    Realm mRealm;

    private int second;

    TimerTask timerTask;

    public NewTestService() {
        Log.i("Test", "Service: constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Test", "Service: onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Test", "Service: onCreate");
        EventBus.getDefault().register(this);
      //  initRealm();
        mRealm = Realm.getDefaultInstance();
        initSocket();
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

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("TAG25", this + " socket connected - " + mSocket.connected());

                if(!mSocket.connected()){
                    Log.i("TAG25", "socket lost connection");
                    wasLost[0] = true;
                } else {
                    if(wasLost[0]){
                        Log.i("TAG25", "socket get connection after lost");
                        wasLost[0] = false;
                        mSocket.off("history");
                        mSocket.disconnect();
                        mSocket.close();
                        this.cancel();
                        initSocket();
                    }
                }
            }
        };

        // check if socket lost connection and get it again. then init again, can be problem with
        new Timer().schedule(timerTask, 5000, 10000);
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

// {"ts":15396854469852,"history":[[6,1539685446,null,null,6],[7,1539685446,6]]}
            // [6,1539685446,null,null,6]
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);

                String info = null;
                switch (array.getInt(0)){
                    case 1: info = "new message   "; break;
                    case 2: info = "was read      "; break;
                    case 5: info = "dialog update "; break;
                    case 6: info = "read dialog   "; break;
                    case 7: info = "общее кол-во непрочит. "; break;
                }

                Log.i("TAG25", info + array.length() + ": " + array.toString());
                int type = array.getInt(0);
                final long userId = array.getLong(2);
                final long time = array.getLong(1);
                int messageId = -1;
                if(array.length()>3){
                    messageId = array.getInt(4);
                }

                Message message;
                Message messageToSend;

                //History - {"ts":15345123848696,"history":[[2,1534512384,3,0,1242]]}

                switch (type){
                    case 1:
                        Log.i("TAG21", "new mes");
                        //How to get text

                        break;
                    case 2:
                        Log.i("TAG25", "execute was read");
                 /*       message = mRealm
                                .where(Message.class).equalTo("id", messageId).findFirst();
                        mRealm.beginTransaction();
                        if(message != null){
                            message.setWasRead(true);
                        }
                        mRealm.commitTransaction();*/
                        EventBus.getDefault().post(new EventBusMessages.MessageWasRead(messageId));
                        break;
                    case 5:
                        //new message
                        final String text = array.getString(5);
                        final int out = array.getInt(6);
                        Log.i("TAG25", "execute dialog update out - " + out);

                            message = new Message();
                            message.setId(messageId);
                            message.setUser(userId);
                            message.setTime(time);
                            message.setText("from s - " + text);
                            message.setOut(out);
                            message.setWasSended(true);
                            message.setWasRead(false);
                            EventBus.getDefault().post(new EventBusMessages.NewChatMessage(message, out));


                        if(!((App) getApplicationContext()).isAppForeground()){
                            Log.i("TAG21", "isNotForeground");
                            if(out == 0){
                                Log.i("TAG21", "send Notification");
                                //   sendNotification(text, String.valueOf(userId));
                                generateNotification(text, String.valueOf(userId));
                            }
                        }

                        break;
                }
            }
            //newRequest();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("TAG21", "JSON GET RESPONSE ERROR");
            Log.i("TAG25", "JSON GET RESPONSE ERROR");
            //     newRequest();
        }

               /* JSONObject innerErrorObject;
                try {
                    innerErrorObject = jsonObject.getJSONObject("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

    }

    public void sendNotification(String text, String userId) {

        if(isNotificationVisible()){
            Log.i("TAG21", "notification already exist");
        } else {
            Log.i("TAG21", "notification not exist");
        }

        Intent notificationIntent = new Intent(this, ChatActivity.class);
        notificationIntent.putExtra("user_id", userId);

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();

        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.mipmap.ic_main)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle("Сообщение от " + userId)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(text) // Текст уведомления
                // необязательные настройки
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_main)) // большая
                // картинка
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                //.setTicker("Последнее китайское предупреждение!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true); // автоматически закрыть уведомление после нажатия

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Альтернативный вариант
        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(23, builder.build());
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

    private void generateNotification(String text, String userId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_main)
                        .setContentTitle("Message from "+ userId)
                        .setContentText(text);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ChatActivity.class);

      //  resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        resultIntent.putExtra("user_id", Long.parseLong(userId));

        mBuilder.setAutoCancel(true);

        mBuilder.setContentText(text).setNumber(Integer.parseInt(SharedManager.getProperty("unread_" + userId)));


// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ChatActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(Integer.parseInt(userId), mBuilder.build());



/*       NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Sets an ID for the notification, so it can be updated
        int notifyID = 1;
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_check);
       int numMessages = 5;
// Start of a loop that processes data and then notifies the user

        mNotifyBuilder.setContentText(text)
                .setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());*/
    }

    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        PendingIntent test = PendingIntent.getService(this, 23, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "Service: onStartCommand. Intent - " + intent.getStringExtra("data") );
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);

        Intent intent = new Intent(this, getClass());
        intent.putExtra("data", "restart");
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), + 3000, pintent);
        super.onDestroy();
        Log.i("Test", "Service: restart");
        Log.i("Test", "Service: onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Test", "Service: onTaskRemoved");
    }
}
