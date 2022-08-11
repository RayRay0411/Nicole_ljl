package com.app.book.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator
 */

public class PreferencesService {
    private Context context;
    private SharedPreferences preferences;

    public PreferencesService(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("mall", Context.MODE_PRIVATE);
    }

    public void save(String key, String val){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public void remove(String key){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public String get(String key){
        return preferences.getString(key, "");
    }
}
