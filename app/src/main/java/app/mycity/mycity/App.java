package app.mycity.mycity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;

import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.ChatActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;
    public boolean isChatActivityStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedManager.init(this);
        initRealm();

        registerActivityLifecycleCallbacks(this);
    }

    public void showAlertDialog(Context context, String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(s);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("chat.realm_" + SharedManager.getProperty(Constants.KEY_LOGIN))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public boolean isAppForeground() {
        return activityCount > 0;
    }

    public boolean isChatActivityStarted() {
        return isChatActivityStarted;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        activityCount++;
        if (activity instanceof ChatActivity) {
            isChatActivityStarted = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        activityCount--;
        if (activity instanceof ChatActivity) {
            isChatActivityStarted = false;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }
}
