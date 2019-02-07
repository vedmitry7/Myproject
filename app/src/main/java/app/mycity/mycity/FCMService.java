package app.mycity.mycity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import app.mycity.mycity.views.activities.ChatActivity;
import app.mycity.mycity.views.activities.MainActivity3;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // TODO: send your new token to the server
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("TAG26", "onMessageReceived ");
        String from = remoteMessage.getFrom();
        Map data = remoteMessage.getData();

        if (data != null) {
            // TODO: handle your message and data

            String title = (String) data.get("title");
            String message = (String) data.get("body");

            Log.d("TAG26", "Eeeee - " + from + " t - " + remoteMessage.getNotification().getTitle() + " m - " + remoteMessage.getNotification().getBody());
            // remoteMessage.getNotification().getTitle();

            if (data != null) {
                // TODO: handle your message and data
                String type = (String) data.get("type");
                String peerId = (String) data.get("peer_id");
                Log.i("TAG26", "Type " + type);

                switch (type) {
                    case "new_message":
                        if (((App) getApplicationContext()).isChatActivityStarted) {
                            Log.i("TAG26", "chat started");
                            Log.i("TAG26", "cur chat user - " + ((App) getApplicationContext()).getCurrentChatUser());
                            if (!((App) getApplicationContext()).getCurrentChatUser().equals(peerId)) {
                                sendMessageNotification(remoteMessage, type);
                            }
                        } else {
                            Log.i("TAG26", "chat not started");
                            sendMessageNotification(remoteMessage, type);
                        }
                        break;
                    case "like_post":
                        sendMessageNotification(remoteMessage, type);
                        break;
                    case "like_comment":
                        sendMessageNotification(remoteMessage, type);
                        break;
                    case "comment_post":
                        sendMessageNotification(remoteMessage, type);
                        break;
                    case "follow":
                        sendMessageNotification(remoteMessage, type);
                        break;
                }
            }
        }
    }

    private void sendMessageNotification(RemoteMessage remoteMessage, String type) {
        // TODO: show notification using NotificationCompat
        Log.i("TAG26", "send notification " + type);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_main)
                        .setContentTitle("F " + remoteMessage.getNotification().getTitle())
                        .setContentText("F " + remoteMessage.getNotification().getBody());
        //  mBuilder.setSound(alarmSound);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        long[] vibrate = { 0, 200, 100, 100, 100, 50};
        mBuilder.setVibrate(vibrate);

        Map data = remoteMessage.getData();

        Intent resultIntent = null;

        switch (type){
            case "new_message":
                resultIntent = new Intent(this, ChatActivity.class);
                String peerId = (String) data.get("peer_id");
                resultIntent.putExtra("peer_id", peerId);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                break;
            case "like_post":
                resultIntent = new Intent(this, MainActivity3.class);
                resultIntent.putExtra("type", type);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                break;
        }


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String nId = (String) data.get("peer_id");
        if(nId==null){
            nId = "255";
        }
        mNotificationManager.notify(Integer.parseInt(nId), mBuilder.build());

    }
}
