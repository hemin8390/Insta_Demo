package com.hemin_practical.Utils;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.hemin_practical.Database.DatabaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Hemin Shah on 24-Jun-17.
 */

public class InstaApplication extends Application {

    public static final String TAG = InstaApplication.class.getSimpleName();

    private static InstaApplication mInstance;

    public static DatabaseHelper helper;
    public static SQLiteDatabase db;


    public static synchronized InstaApplication getInstance() {
        return mInstance;
    }

    static public void exportDatabse(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "insta_data.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        helper = new DatabaseHelper(getApplicationContext());
        db = helper.getWritableDatabase();

        exportDatabse(getApplicationContext(), "Insta_Demo.db");
    }
}