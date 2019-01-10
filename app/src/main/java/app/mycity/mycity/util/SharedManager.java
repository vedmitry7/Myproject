package app.mycity.mycity.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedManager {

    public static final String STORAGE_NAME = "my_city";

    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init( Context context ){
        SharedManager.context = context;
    }

    private static void init(){
        settings = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void addProperty(String name, String value ){
        if( settings == null ){
            init();
        }
        editor.putString( name, value );
        editor.commit();
    }

    public static String getProperty(String name ){
        if( settings == null ){
            init();
        }
        return settings.getString( name, null );
    }


    public static boolean getBooleanProperty(String name ){
        if( settings == null ){
            init();
        }
        return settings.getBoolean( name, false);
    }

    public static void addBooleanProperty(String name, boolean value ){
        if( settings == null ){
            init();
        }
        editor.putBoolean( name, value );
        editor.commit();
    }

    public static int getIntProperty(String name ){
        if( settings == null ){
            init();
        }
        return settings.getInt( name, -1);
    }

    public static void addIntProperty(String name, int value ){
        if( settings == null ){
            init();
        }
        editor.putInt( name, value );
        editor.commit();
    }
}
