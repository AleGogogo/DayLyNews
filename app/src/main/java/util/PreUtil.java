package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rain on 2016/10/13.
 */

public class PreUtil {

    public static void putStringtoDefault(Context context,String key,String values){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(key,values);
    }

    public static String getStringFromDefault(Context context, String key, String defValue){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return  sp.getString(key,defValue);
    }
}
