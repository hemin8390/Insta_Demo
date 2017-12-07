package com.hemin_practical.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hemin_practical.Database.DataManager;
import com.hemin_practical.R;
import com.hemin_practical.Utils.ApplicationData;
import com.hemin_practical.Utils.ClsGeneral;
import com.hemin_practical.Utils.InstagramApp;
import com.hemin_practical.Utils.UserPrefs;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonLogin, buttonLogout, buttonMedia;
    LinearLayout layout_hide_show;
    TextView textUserName, textFullName, textBio;
    CircleImageView imageUser;

    private InstagramApp mApp;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();

    UserPrefs up;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == InstagramApp.WHAT_FINALIZE) {
                userInfoHashmap = mApp.getUserInfo();
                up.storeID(userInfoHashmap.get(InstagramApp.TAG_ID));
                cursor = data_manager.getUser(userInfoHashmap.get(InstagramApp.TAG_ID));
                startManagingCursor(cursor);
                if (cursor.getCount() > 0) {
                    UpdateData();
                } else {
                    AddDataLocally();
                }
            } else if (msg.what == InstagramApp.WHAT_ERROR) {
                Toast.makeText(HomeActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    Cursor cursor;
    DataManager data_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        up = new UserPrefs(HomeActivity.this);

        data_manager = DataManager.getInstance(getApplicationContext());

        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("version1", "" + Build.VERSION.SDK_INT);
            if (checkPermission()) {
            } else {
                requestPermission();
            }
        } else {
            Log.e("version", "" + Build.VERSION.SDK_INT);
        }

        PrepareViews();

        mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
                ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {
                buttonLogin.setVisibility(View.GONE);
                buttonLogout.setVisibility(View.VISIBLE);
                layout_hide_show.setVisibility(View.VISIBLE);
                mApp.fetchUserName(handler);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        if (mApp.hasAccessToken()) {
            buttonLogin.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.VISIBLE);
            layout_hide_show.setVisibility(View.VISIBLE);
            if (ClsGeneral.checkInternetConnection(HomeActivity.this)) {
                mApp.fetchUserName(handler);
            } else {
                if (!up.getApiId().isEmpty()) {
                    cursor = data_manager.getUser(up.getApiId());
                    startManagingCursor(cursor);
                    if (cursor.getCount() > 0) {
                        InitializeViews();
                    } else {
                    }
                }
            }
        }
    }

    public void PrepareViews() {
        layout_hide_show = (LinearLayout) findViewById(R.id.layout_hide_show);

        buttonLogin = (Button) findViewById(R.id.button_insta_login);
        buttonLogout = (Button) findViewById(R.id.button_insta_logout);
        buttonMedia = (Button) findViewById(R.id.buttonMedia);
        buttonLogin.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        buttonMedia.setOnClickListener(this);

        textUserName = (TextView) findViewById(R.id.textView_insta_username);
        textFullName = (TextView) findViewById(R.id.textView_insta_fullname);
        textBio = (TextView) findViewById(R.id.textview_insta_Bio);

        imageUser = (CircleImageView) findViewById(R.id.image_user_profile);

    }

    public void InitializeViews() {
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                userInfoHashmap.put(InstagramApp.TAG_ID, cursor.getString(0));
                userInfoHashmap.put(InstagramApp.TAG_BIO, cursor.getString(1));
                userInfoHashmap.put(InstagramApp.TAG_FOLLOWED_BY, cursor.getString(2));
                userInfoHashmap.put(InstagramApp.TAG_FOLLOWS, cursor.getString(3));
                userInfoHashmap.put(InstagramApp.TAG_FULL_NAME, cursor.getString(4));
                userInfoHashmap.put(InstagramApp.TAG_MEDIA, cursor.getString(5));
                userInfoHashmap.put(InstagramApp.TAG_USERNAME, cursor.getString(6));
                userInfoHashmap.put(InstagramApp.TAG_PROFILE_PICTURE, cursor.getString(7));
            }

            if (!userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE).isEmpty()) {
                Glide.with(HomeActivity.this)
                        .load(userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageUser);
            }

            textUserName.setText(userInfoHashmap.get(InstagramApp.TAG_USERNAME));
            textFullName.setText(userInfoHashmap.get(InstagramApp.TAG_FULL_NAME));
            textBio.setText("Bio : \n" + userInfoHashmap.get(InstagramApp.TAG_BIO) + "\n\n" +
                    "Followers : " + userInfoHashmap.get(InstagramApp.TAG_FOLLOWED_BY) +
                    "\n" + "Following : " + userInfoHashmap.get(InstagramApp.TAG_FOLLOWS));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogin) {
            loginOrlogoutUser();
        }

        if (view == buttonLogout) {
            loginOrlogoutUser();
        }

        if (view == buttonMedia) {
            startActivity(new Intent(HomeActivity.this, AllMedia.class)
                    .putExtra("userInfo", userInfoHashmap));
        }
    }

    private void loginOrlogoutUser() {
        if (mApp.hasAccessToken()) {
            if (ClsGeneral.checkInternetConnection(HomeActivity.this)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        HomeActivity.this);
                builder.setMessage("Disconnect from Instagram?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mApp.resetAccessToken();
                                        buttonLogin.setVisibility(View.VISIBLE);
                                        buttonLogout.setVisibility(View.GONE);
                                        layout_hide_show.setVisibility(View.GONE);
                                        ResetData();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                handler.sendEmptyMessage(InstagramApp.WHAT_ERROR);
            }
        } else {
            if (ClsGeneral.checkInternetConnection(HomeActivity.this)) {
                mApp.authorize();
            } else {
                handler.sendEmptyMessage(InstagramApp.WHAT_ERROR);
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted && writeAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void AddDataLocally() {
        long row_id;

        row_id = data_manager.AddUser(userInfoHashmap.get(InstagramApp.TAG_ID),
                Integer.parseInt(userInfoHashmap.get(InstagramApp.TAG_FOLLOWED_BY)),
                Integer.parseInt(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS)),
                userInfoHashmap.get(InstagramApp.TAG_BIO),
                userInfoHashmap.get(InstagramApp.TAG_FULL_NAME),
                Integer.parseInt(userInfoHashmap.get(InstagramApp.TAG_MEDIA)),
                userInfoHashmap.get(InstagramApp.TAG_USERNAME),
                userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE));

        if (row_id != 0) {
            cursor = data_manager.getUser(userInfoHashmap.get(InstagramApp.TAG_ID));
            InitializeViews();
        }
    }

    public void UpdateData() {
        long row_id;

        row_id = data_manager.updateData(userInfoHashmap.get(InstagramApp.TAG_ID),
                Integer.parseInt(userInfoHashmap.get(InstagramApp.TAG_FOLLOWED_BY)),
                Integer.parseInt(userInfoHashmap.get(InstagramApp.TAG_FOLLOWS)),
                userInfoHashmap.get(InstagramApp.TAG_BIO),
                userInfoHashmap.get(InstagramApp.TAG_FULL_NAME),
                Integer.parseInt(userInfoHashmap.get(InstagramApp.TAG_MEDIA)),
                userInfoHashmap.get(InstagramApp.TAG_USERNAME),
                userInfoHashmap.get(InstagramApp.TAG_PROFILE_PICTURE));

        if (row_id != 0) {
            cursor = data_manager.getUser(userInfoHashmap.get(InstagramApp.TAG_ID));
            InitializeViews();
        }
    }

    public void ResetData() {
        long rowid = data_manager.deleteData(up.getApiId());
        if (rowid == 0) {
            Log.e("Data", "deleted");
        }
        up.resetData();
    }
}
