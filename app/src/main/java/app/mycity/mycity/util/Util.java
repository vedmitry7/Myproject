package app.mycity.mycity.util;


import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {

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


    public static String getDate(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));

        return sdf.format(date);
    }

    private static String getTime(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));

        return sdf.format(date);
    }

    public static String getDatePretty(long time) {
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
