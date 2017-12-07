package com.hemin_practical.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hemin_practical.R;
import com.hemin_practical.Utils.ClsGeneral;
import com.hemin_practical.Utils.InstagramApp;
import com.hemin_practical.Utils.JSONParser;
import com.hemin_practical.Utils.UserPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllMedia extends AppCompatActivity {

    private HashMap<String, String> userInfo;
    private ArrayList<String> imageThumbList = new ArrayList<String>();
    private Context context;
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";

    UserPrefs up;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == WHAT_FINALIZE) {
//                setImageGridAdapter();
                imageList.setAdapter(new DataAdapter(context, imageThumbList));
            } else {
                Toast.makeText(context, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    RecyclerView imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_media);

        up = new UserPrefs(AllMedia.this);

        imageList = (RecyclerView) findViewById(R.id.imageList);
        imageList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        imageList.setLayoutManager(layoutManager);

        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");

        context = AllMedia.this;

        GetData();

    }

    private void getAllMediaImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int what = WHAT_FINALIZE;
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = jsonParser.getJSONFromUrlByGet("https://api.instagram.com/v1/users/self/media/recent?access_token="
                            + InstagramApp.mAccessToken);
                    JSONArray data = jsonObject.getJSONArray(TAG_DATA);
                    up.storeData(data.toString());
                    if (imageThumbList != null || imageThumbList.size() > 0) {
                        imageThumbList.clear();
                    }
                    for (int data_i = 0; data_i < data.length(); data_i++) {
                        JSONObject data_obj = data.getJSONObject(data_i);

                        JSONObject images_obj = data_obj
                                .getJSONObject(TAG_IMAGES);

                        JSONObject thumbnail_obj = images_obj
                                .getJSONObject(TAG_THUMBNAIL);

                        String str_url = thumbnail_obj.getString(TAG_URL);
                        imageThumbList.add(str_url);
                    }

                    System.out.println("jsonObject::" + jsonObject);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    what = WHAT_ERROR;
                }
                // pd.dismiss();
                handler.sendEmptyMessage(what);
            }
        }).start();
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        private ArrayList<String> android;
        private Context context;

        public DataAdapter(Context context, ArrayList<String> android) {
            this.android = android;
            this.context = context;
        }

        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_list_inflater, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

            Glide.with(context)
                    .load(imageThumbList.get(i))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.img_android);
        }

        @Override
        public int getItemCount() {
            return android.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView img_android;

            public ViewHolder(View view) {
                super(view);

                img_android = (ImageView) view.findViewById(R.id.ivImage);
            }
        }

    }

    public void GetData() {
        if (ClsGeneral.checkInternetConnection(getApplicationContext())) {
            getAllMediaImages();
        } else {
            if (up.getData().isEmpty()) {
                handler.sendEmptyMessage(InstagramApp.WHAT_ERROR);
            } else {
                JSONArray data = null;
                try {
                    data = new JSONArray(up.getData());
                    if (imageThumbList != null || imageThumbList.size() > 0) {
                        imageThumbList.clear();
                    }
                    for (int data_i = 0; data_i < data.length(); data_i++) {
                        JSONObject data_obj = data.getJSONObject(data_i);

                        JSONObject images_obj = data_obj
                                .getJSONObject(TAG_IMAGES);

                        JSONObject thumbnail_obj = images_obj
                                .getJSONObject(TAG_THUMBNAIL);

                        String str_url = thumbnail_obj.getString(TAG_URL);
                        imageThumbList.add(str_url);
                    }
                    int what = WHAT_FINALIZE;
                    handler.sendEmptyMessage(what);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            GetData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
