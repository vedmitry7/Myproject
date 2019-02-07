package app.mycity.mycity;

import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import app.mycity.mycity.util.SharedManager;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token  = FirebaseInstanceId.getInstance().getToken();
        Log.i("TAG26", "Refreshed token: " + token);


        SharedManager.addProperty("fcm_token", token);
        sendRegistrationTokenToServer(token);
    }

    private void sendRegistrationTokenToServer(String token) {
        // TODO: Implement this method to send any registration to your app's servers.

    }
}