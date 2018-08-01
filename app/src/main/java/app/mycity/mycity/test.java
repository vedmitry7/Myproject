package app.mycity.mycity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class test {

    public static void main(String[] args) {
        System.out.println(getDate(1532436719));

    }

    private static String getDate(long time) {
        Date date = new Date(Calendar.getInstance().getTimeInMillis()-(1000*60*60*24)); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm"); // the format of your date
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT-2"));

        return sdf.format(date);
    }


}
