package app.mycity.mycity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.ChatActivity;
import app.mycity.mycity.views.activities.ChatActivity2;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;
    public boolean isChatActivityStarted = false;
    String currentChatUser;

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

    public String getCurrentChatUser() {
        return currentChatUser;
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
        Log.i("TAG21", "onActivityResumed");
        activityCount++;
        if (activity instanceof ChatActivity2) {
            isChatActivityStarted = true;
            currentChatUser = ((ChatActivity2)activity).getCurrentChatUser();
            Log.i("TAG21", "onActivityResumed - chat");

        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        activityCount--;
        if (activity instanceof ChatActivity2) {
            isChatActivityStarted = false;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    public static String accessToken() {
        return SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
