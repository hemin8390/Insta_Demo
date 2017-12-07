package com.hemin_practical.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPrefs {

    private static final String SHARED = "insta_demo_prefs";
    private static final String API_IMAGE_DATA = "json_data";
    private static final String API_ID = "id";
    private SharedPreferences sharedPref;
    private static UserPrefs userPrefs;
    private Editor editor;

    public UserPrefs(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    /*public static UserPrefs getInstance(Context context) {
        if (userPrefs == null) {
            userPrefs = new UserPrefs(context);
        }
        return userPrefs;
    }*/

    public void storeID(String id) {
        editor.putString(API_ID, id);
        editor.commit();
    }

    public void storeData(String json) {
        editor.putString(API_IMAGE_DATA, json);
        editor.commit();
    }

    public void resetData() {
        editor.putString(API_ID, "");
        editor.putString(API_IMAGE_DATA, "");
        editor.commit();
    }

    public String getData() {
        return sharedPref.getString(API_IMAGE_DATA, "");
    }

    public String getApiId() {
        return sharedPref.getString(API_ID, "");
    }
}