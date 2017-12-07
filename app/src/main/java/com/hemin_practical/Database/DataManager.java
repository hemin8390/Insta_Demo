package com.hemin_practical.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hemin_practical.Utils.InstaApplication;
import com.hemin_practical.Utils.InstagramApp;

public class DataManager {
    private static int maxPosition = 0;
    private static DataManager manager;
    private SQLiteDatabase db;

    public DataManager(Context context) {
    }


    public static DataManager getInstance(Context context) {
        if (manager == null) {
            manager = new DataManager(context);
        }
        return manager;
    }

    public long AddUser(String U_Id, int follower, int following, String bio,
                        String full_name, int media, String user_name, String image) {

        ContentValues values = new ContentValues();
        values.put(InstagramApp.TAG_ID, U_Id);
        values.put(InstagramApp.TAG_FOLLOWED_BY, follower);
        values.put(InstagramApp.TAG_FOLLOWS, following);
        values.put(InstagramApp.TAG_BIO, bio);
        values.put(InstagramApp.TAG_FULL_NAME, full_name);
        values.put(InstagramApp.TAG_MEDIA, media);
        values.put(InstagramApp.TAG_USERNAME, user_name);
        values.put(InstagramApp.TAG_PROFILE_PICTURE, image);
        // Inserting Row
        long rowID = InstaApplication.db.insert(DatabaseHelper.TABLE_USER.SQL_TABLE_NAME, null, values);

        return rowID;
    }

    public Cursor getUser(String u_id) {
        return InstaApplication.db.query(DatabaseHelper.TABLE_USER.SQL_TABLE_NAME, null,
                InstagramApp.TAG_ID
                        + " = " + "'" + u_id + "'", null, null, null, null);
    }

    public long deleteData(String id) {
        Cursor cursor = InstaApplication.db.query(DatabaseHelper.TABLE_USER.SQL_TABLE_NAME, new String[]{
                        InstagramApp.TAG_ID},
                InstagramApp.TAG_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst()) {
            InstaApplication.db.delete(DatabaseHelper.TABLE_USER.SQL_TABLE_NAME, InstagramApp.TAG_ID
                    + " = " + id, null);
        }
        cursor.close();
        return 0;
    }

    public long updateData(String Id, int follower, int following, String bio,
                           String full_name, int media, String user_name, String image) {
        ContentValues values = new ContentValues();
        values.put(InstagramApp.TAG_FOLLOWED_BY, follower);
        values.put(InstagramApp.TAG_FOLLOWS, following);
        values.put(InstagramApp.TAG_BIO, bio);
        values.put(InstagramApp.TAG_FULL_NAME, full_name);
        values.put(InstagramApp.TAG_MEDIA, media);
        values.put(InstagramApp.TAG_USERNAME, user_name);
        values.put(InstagramApp.TAG_PROFILE_PICTURE, image);
        long rowID = InstaApplication.db.update(DatabaseHelper.TABLE_USER.SQL_TABLE_NAME, values,
                InstagramApp.TAG_ID + " = " + Id, null);

        return rowID;
    }
}