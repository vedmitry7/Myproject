package app.mycity.mycity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import app.mycity.mycity.util.EventBusMessages;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("TAG25", "BOOT - intent - " + intent.getAction());
        if(App.isOnline(context)) {
            Log.d("TAG25", "BOOT - ONLINE DEVICE intent - " + intent.getAction());
            // Toast.makeText(context, "Network Available", Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new EventBusMessages.LocationResume());
            Intent serviceIntent = new Intent(context, SocketService.class);
            serviceIntent.putExtra("data", "receiver");
            context.startService(serviceIntent);
            startLocationService(context);
        } else {
            Log.d("TAG25", "BOOT - OFF LINE");
            //  Toast.makeText(context, "Network is not Available", Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new EventBusMessages.LocationStop());
        }
    }

    private void startLocationService(Context context){
        Intent locServiceIntent = new Intent(context, LocationService.class);
        context.startService(locServiceIntent);
    }
}