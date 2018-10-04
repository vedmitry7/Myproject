package app.mycity.mycity;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.MainActivity2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleApiClient apiClient = null;
    LocationRequest mLocationRequest = null;
    private int locationInterval, fastedInterval;

    List<Place> basePlaces = new ArrayList<>();

    Map<Integer, Integer> counterMap = new HashMap<>();

    boolean check;

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
        Log.i("TAG23", "LocationService: onCreate");

        locationInterval = 60*1000;
        fastedInterval = 60*1000;
        setLocationLocationRequest();
    }


    private void setLocationLocationRequest() {

        try {
            apiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(locationInterval);
            mLocationRequest.setFastestInterval(fastedInterval);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            apiClient.connect();

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

        Intent intent = new Intent(this, MainActivity2.class);
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


    @Override
    public void onLocationChanged(Location location) {
        // After your desired interval This api will give you the Location Object.
        Log.d("TAG23", "Location Location Changed ");
        Log.d("TAG23", "Long - " + location.getLongitude() + " lat - " + location.getLatitude());

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

    }

    @Override
    public void onDestroy() {
        // Your need of location update is done. So you have to stop the apiClient.
        super.onDestroy();
        this.apiClient.disconnect();
    }
}
