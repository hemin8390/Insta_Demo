package com.hemin_practical.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hemin_practical.Utils.InstagramApp;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Insta_Demo.db";
    public static final String SQL_CREATE_TABLE_FARE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USER.SQL_TABLE_NAME + "( " + InstagramApp.TAG_ID
            + " TEXT , "
            + InstagramApp.TAG_BIO + " TEXT,"
            + InstagramApp.TAG_FOLLOWED_BY + " INTEGER,"
            + InstagramApp.TAG_FOLLOWS + " INTEGER,"
            + InstagramApp.TAG_FULL_NAME + " TEXT,"
            + InstagramApp.TAG_MEDIA + " INTEGER,"
            + InstagramApp.TAG_USERNAME + " TEXT,"
            + InstagramApp.TAG_PROFILE_PICTURE + " TEXT );";
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(DatabaseHelper.SQL_CREATE_TABLE_FARE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.db = db;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER.SQL_TABLE_NAME);
    }

    public static class TABLE_USER {
        public static final String SQL_TABLE_NAME = "user_data";
    }
}