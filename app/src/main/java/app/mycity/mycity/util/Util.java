package app.mycity.mycity.util;


import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
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



}
