package app.mycity.mycity.util;


import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Place;

public class Util {

    public static void indicateTabImageView(Context context, View v, int pos){
        ImageView imageView;
        for (int i = 0; i < 4; i++) {
            imageView = v.findViewById(Constants.navButtonsIcons[i]);
            if(i==pos){
                imageView.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            } else {
                imageView.setColorFilter(context.getResources().getColor(R.color.colorDefaultButton));
            }
        }

    }

    public static void setOnTabClick(View v){
        RelativeLayout layout;
        for (int i = 0; i < 4; i++) {
            layout = v.findViewById(Constants.navButtons[i]);
            final int finalI = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.SwichTab(finalI));
                }
            });
        }
    }



    public static String convertToString(Place place){
        String serializedObject = "";

        // serialize the object
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream so = new ObjectOutputStream(bo);
                so.writeObject(place);
                so.flush();
                serializedObject = bo.toString();
            } catch (IOException e) {
                Log.d("TAG21", "CONVERTED error- " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        Log.d("TAG21", "CONVERTED - " + serializedObject);
            return serializedObject;
    }
    public static Place convertToPlace(String place){
        Place obj = null;
        try {
            byte b[] = Base64.decode(place.getBytes(), 0);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            obj = (Place) si.readObject();
        } catch (IOException e) {
            Log.d("TAG21", "CONVERTED error- " + e.getMessage());
            Log.d("TAG21", "CONVERTED error- " + e.getLocalizedMessage());
            Log.d("TAG21", "CONVERTED error- " + e.toString());
            Log.d("TAG21", "CONVERTED error- " + e.getCause());
        } catch (ClassNotFoundException e) {
            Log.d("TAG21", "Class cast error- " + e.getCause());
            e.printStackTrace();
        }
        return obj;
    }



    public static String getFileName(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = "/checkin_" + formatter.format(now) + ".jpg";
        return fileName;
    }

    public static String getExternalFileName(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.US);
        Date now = new Date();

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "MyCity");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }

        String fileName;
        // if add folder file will not created
        if(success){
            fileName = Environment.getExternalStorageDirectory() + "/MyCity/checkin_" + formatter.format(now) + ".png";
        } else {
            fileName = Environment.getExternalStorageDirectory() + "/checkin_" + formatter.format(now) + ".png";
        }
        return fileName;
    }


    public static String getDate_ddMMyyyy(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
       // SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm"); // the format of your date
       SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));

        return sdf.format(date);
    }

    public static String getTime(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));

        return sdf.format(date);
    }

    public static String getDatePretty(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        String niceDateStr = (String) DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
        return niceDateStr;
    }


    public static String getDatePrettyOld(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm"); // the format of your date
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(time*1000L);

        Date curDate = new Date(cdate.getTimeInMillis());
        if(isToday(time*1000L)){
            return formatTime.format(date);
        }
        if(isYesterday(time*1000L)){
            return "Yesterday";
        }

        return sdf.format(date);
    }

    public static boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE,-1);



        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

    public static boolean isToday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

}
