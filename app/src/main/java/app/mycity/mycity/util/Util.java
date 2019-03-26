package app.mycity.mycity.util;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Place;

public class Util {


    int countLineBreaks(final TextView textView, final String toMeasure) {

        final Paint paint = textView.getPaint(); // Get the paint used by the TextView
        int startPos = 0;
        int breakCount = 0;
        final int endPos = toMeasure.length();

        int lineCount = 0;

        // Loop through the string, moving along the number of characters that will
        // fit on a line in the TextView. The number of iterations = the number of line breaks

        while (startPos < endPos) {
            startPos += paint.breakText(toMeasure.substring(startPos, endPos),
                    true,  textView.getWidth(),(float[]) null);
            lineCount++;
        }
        // Line count will now equal the number of line-breaks the string will require
        return lineCount;
    }

    public static void setNawBarClickListener(View v){
        ImageView imageView = v.findViewById(R.id.makeCheckinPhoto);
        

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.MakeCheckin());
            }
        });

        ConstraintLayout layout;
        for (int i = 0; i < 4; i++) {
            layout = v.findViewById(Constants.newNavButtons[i]);
            final int finalI = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI){
                        case 0:
                            EventBus.getDefault().post(new EventBusMessages.OpenMenu());
                            Log.d("TAG21", "click open menu");
                            break;
                        case 1:
                            EventBus.getDefault().post(new EventBusMessages.OpenUser(SharedManager.getProperty(Constants.KEY_MY_ID)));
                            Log.d("TAG21", "click open pr");
                            break;
                        case 2:
                            EventBus.getDefault().post(new EventBusMessages.OpenChat());
                            Log.d("TAG21", "click open chat");
                            break;
                        case 3:
                            EventBus.getDefault().post(new EventBusMessages.OpenNotifications());
                            Log.d("TAG21", "click open notif");
                            break;
                    }
                }
            });
        }


    }

    public static void setNawBarIconColor(Context context, View v, int pos){
        ImageView imageView;
        for (int i = 0; i < 4; i++) {
            imageView = v.findViewById(Constants.newNavButtonsIcons[i]);
            if(i==pos){
                imageView.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            } else {
                imageView.setColorFilter(context.getResources().getColor(R.color.black_67percent));
            }
        }
    }

  public static void setUnreadCount(View view){

      View indicatorView = view.findViewById(R.id.unreadMessageIndicator);

      if(SharedManager.getBooleanProperty("unreadMessages")){
          indicatorView.setVisibility(View.VISIBLE);
      } else {
          indicatorView.setVisibility(View.GONE);
      }


  /*    TextView textView = view.findViewById(R.id.totalUnreadCount);
      if(SharedManager.getIntProperty("totalUnreadCount")>0){
          textView.setText(" " + SharedManager.getIntProperty("totalUnreadCount"));
          textView.setVisibility(View.VISIBLE);
      } else {
          textView.setVisibility(View.GONE);
      }*/
      Log.i("TAG25", "SHOW UNREAD - " + SharedManager.getIntProperty("totalUnreadCount"));
  }


  public static View.OnTouchListener getTouchTextListener(final TextView textView){

        final ColorStateList color = textView.getTextColors();
      View.OnTouchListener onTouchListener = new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
              if(event.getAction()== MotionEvent.ACTION_DOWN){
                  textView.setTextColor(Color.parseColor("#000000"));
              }
              if(event.getAction()== MotionEvent.ACTION_UP){
                  textView.setTextColor(Color.parseColor("#999999"));
              }
              return false;
          }
      };
      return onTouchListener;
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

    public static String getExternalVideoFileName(){
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
            fileName = Environment.getExternalStorageDirectory() + "/MyCity/checkin_" + formatter.format(now) + ".mp4";
        } else {
            fileName = Environment.getExternalStorageDirectory() + "/checkin_" + formatter.format(now) + "mp4";
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

    public static String getTimeForLog(long time) {
        Date date = new Date(time); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM hh:mm"); // the format of your date
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
