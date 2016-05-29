package com.illusionbox.www.chronicle;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Maleen on 5/29/2016.
 */
public class ChroniclePreferences {

    static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    static void setPreference(Context context, String key, String value){
        context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    static String getPreference(Context context, String key){
        return getSharedPreferences(context).getString(key, null);
    }
}
