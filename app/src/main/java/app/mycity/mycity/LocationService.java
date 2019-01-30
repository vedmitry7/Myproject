package app.mycity.mycity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.MainActivity3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient apiClient = null;
    LocationRequest mLocationRequest = null;
    private int locationInterval, fastedInterval;
    private int second;


    public LocationService() {
        Log.d("TAG23", "Location Service constructor" );
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TAG23", "Location Service bind" );
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Test", "LocationService: onCreate");

        if(!SharedManager.getBooleanProperty("login")){
            Log.i("Test", "onCreate. Login false Service: STOP SELF");
            stopSelf();
            return;
        } else {
            Log.i("Test", "onCreate. Login true");
        }

        Log.i("Test", "onCreate. continue");

        EventBus.getDefault().register(this);

        locationInterval = 60*10000;
        fastedInterval = 60*10000;
        setLocationLocationRequest();

        Intent ishintent = new Intent(this, LocationService.class);
        ishintent.putExtra("data", "alarm");
        PendingIntent pintent = PendingIntent.getService(this, 7, ishintent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),5000, pintent);

 /*       new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("TAG23", "Location count: " + second++ + ", state - " + apiClient.isConnected());
            }
        }, 0, 1000);*/
    }




    private void setLocationLocationRequest() {

        try {
            apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(locationInterval);
            mLocationRequest.setFastestInterval(fastedInterval);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if(App.isOnline(getApplicationContext())){
                apiClient.connect();
            }

        } catch (Exception e) {
            Log.d("TAG23", e.getMessage() == null ? "" : e.getMessage());
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        // Your API Client is connected. So can request for updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d("TAG23", "Location service connected ");
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    void notifyUser(Place place){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());

        Intent intent = new Intent(this, MainActivity3.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("place", place.getName());
        PendingIntent openActivityIntetnt = PendingIntent.getActivity(this,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Подтвердите что вы находитесь в:")
                .setContentText(place.getName())
                //.setVibrate(new long[]{500, 500})
                .setContentIntent(openActivityIntetnt)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(43, notificationBuilder.build()); //

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UpdateCoordinates event){
        Log.d("TAG23", "Location Location Changed");
        this.apiClient.disconnect();
        apiClient.connect();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.LocationStop event){
        Log.d("TAG23", "Location Stop");
        this.apiClient.disconnect();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.LocationResume event){
        Log.d("TAG23", "Location Resume");
        apiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // After your desired interval This api will give you the Location Object.
        Log.d("TAG23", "Location Location Changed ");
        Log.d("TAG23", "Long - " + location.getLongitude() + " lat - " + location.getLatitude());
        Log.d("Test", "Long - " + location.getLongitude() + " lat - " + location.getLatitude());


        SharedManager.addProperty("latitude", String.valueOf(location.getLatitude()));
        SharedManager.addProperty("longitude", String.valueOf(location.getLongitude()));

        ApiFactory.getApi().setCoordinates(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), location.getLatitude(), location.getLongitude() ).enqueue(new Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<UsersContainer>> call, Response<ResponseContainer<UsersContainer>> response) {
                Log.d("TAG23", "some answer ");
            }

            @Override
            public void onFailure(Call<ResponseContainer<UsersContainer>> call, Throwable t) {
                Log.d("TAG23", "some answer error");
            }
        });

    }

    void setUpdateInterval(int locationInterval, int fastedInterval){

        if(apiClient!=null) this.apiClient.disconnect();

        this.locationInterval = locationInterval;
        this.fastedInterval = fastedInterval;

        setLocationLocationRequest();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("TAG23", "ConnectionFailed");
    }

    @Override
    public void onDestroy() {
        Log.d("Test", "On Destroy Location Service");
        // Your need of location update is done. So you have to stop the apiClient.
        EventBus.getDefault().unregister(this);
        if(apiClient!=null){
            apiClient.disconnect();
        }
        super.onDestroy();
    }
}
