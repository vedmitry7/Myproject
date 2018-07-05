package app.mycity.mycity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedManager.init(this);
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
}
