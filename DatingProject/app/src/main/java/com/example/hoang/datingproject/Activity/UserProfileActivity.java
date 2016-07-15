package com.example.hoang.datingproject.Activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Adapter.FeedsAdapter;
import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.InboxModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.OnLoadMoreListener;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView recyclerView;
    private FeedsAdapter adapter;
    private ArrayList<FeedModel> arr;
    private Button activity_log, user, more;
    private TextView edit_cover,edit_profile, camera, status, music;
    private RelativeLayout edit_cover_photo, edit_profile_photo, new_post;
    private ImageView cover, profile, newPostProfile;
    private Bitmap img = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView nickname;
    private FeedModel feedModel = new FeedModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getControls();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getControls() {

        recyclerView = (RecyclerView) UserProfileActivity.this.findViewById(R.id.recyclerview);
        arr = new ArrayList<FeedModel>();
//        initData();
        adapter = new FeedsAdapter(UserProfileActivity.this, arr, recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UserProfileActivity.this, LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe2refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
//        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                arr.add(null);
//                adapter.notifyItemInserted(arr.size() - 1);
//
//                //Load more data for reyclerview
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        //Remove loading item
//                        arr.remove(arr.size() - 1);
//                        adapter.notifyItemRemoved(arr.size());
//
//                        //Load data
//                        int index = arr.size();
//                        int end = index + 6;
//                        initData();
//                        adapter.notifyDataSetChanged();
//                        adapter.setLoaded();
//                    }
//                }, 5000);
//            }
//        });
        activity_log = (Button) findViewById(R.id.activity_log);
        user = (Button) findViewById(R.id.user);
        more = (Button) findViewById(R.id.more);
        edit_cover = (TextView) findViewById(R.id.edit_cover);
        edit_profile = (TextView) findViewById(R.id.edit_profile);
        camera = (TextView) findViewById(R.id.camera);
        status = (TextView) findViewById(R.id.status);
        music = (TextView) findViewById(R.id.music_icon);
        new_post = (RelativeLayout) findViewById(R.id.new_post_1);

        Typeface font = FontManager.getTypeface(UserProfileActivity.this, FontManager.FONTAWESOME);

        activity_log.setTypeface(font);
        user.setTypeface(font);
        more.setTypeface(font);
        edit_cover.setTypeface(font);
        edit_profile.setTypeface(font);
        camera.setTypeface(font);
        status.setTypeface(font);
        music.setTypeface(font);

        cover = (ImageView) findViewById(R.id.cover);
        profile = (ImageView) findViewById(R.id.profile);
        newPostProfile = (ImageView) findViewById(R.id.new_post_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        feedModel = (FeedModel) bundle.getSerializable("model");

        postFootPrint(PersonalInfoActivity.getDefaults("id", UserProfileActivity.this), feedModel.getUserID());
        String profileImage = feedModel.getFeedIcon();

        nickname = (TextView) findViewById(R.id.nickname);
        nickname.setText(feedModel.getFeedTitle());

        byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        profile.setImageBitmap(decodedByte);
        newPostProfile.setImageBitmap(decodedByte);

        edit_cover_photo = (RelativeLayout) findViewById(R.id.edit_cover_photo);
        edit_profile_photo = (RelativeLayout) findViewById(R.id.edit_profile_photo);

        edit_profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfileImageFromGallery();
            }
        });

        edit_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });

        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, NewNoteActivity.class);
                startActivityForResult(intent, Const.NEW_NOTE);
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, NewNoteActivity.class);
                startActivityForResult(intent, Const.NEW_NOTE);
            }
        });

        new GetData().execute(Const.PROFILE_TIMELINE_URL + feedModel.getUserID() + "&" + Const.LIMIT + "=" + 100 + "&" + Const.OFFSET + "=" + 1);

//        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.container);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout.OnOffsetChangedListener listener = new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Ngoc Hung");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        };

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(listener);
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.COVER_CHOOSE);
    }

    private void getProfileImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.PROFILE_CHOOSE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.COVER_CHOOSE && resultCode == RESULT_OK) {
            if (data != null) {
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String fileSrc = cursor.getString(idx);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                img = BitmapFactory.decodeFile(fileSrc, options);

                //scale size to read
                options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
                options.inJustDecodeBounds = false;
                img = BitmapFactory.decodeFile(fileSrc, options);

                BitmapDrawable ob = new BitmapDrawable(getResources(), img);
                cover.setBackgroundDrawable(ob);
            }
        }else if(requestCode == Const.PROFILE_CHOOSE && resultCode == RESULT_OK) {
            if (data != null) {
                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String fileSrc = cursor.getString(idx);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                img = BitmapFactory.decodeFile(fileSrc, options);

                //scale size to read
                options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
                options.inJustDecodeBounds = false;
                img = BitmapFactory.decodeFile(fileSrc, options);

                BitmapDrawable ob = new BitmapDrawable(getResources(), img);
                profile.setImageDrawable(ob);
            }
        } else if (requestCode == Const.NEW_NOTE && resultCode == Const.RETURN_NEW_NOTE) {
            Bundle bundle = data.getBundleExtra("data");
            FeedModel model = (FeedModel) bundle.getSerializable("feed_item");
            byte[] image = bundle.getByteArray("img");
            Bitmap img = BitmapFactory.decodeByteArray(image, 0, image.length);
            model.setImage(img);
            arr.add(model);
            adapter.notifyDataSetChanged();
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            });
        }
    }

    @Override
    public void onRefresh() {

        recyclerView.post(new Runnable() {
            @Override
            public void run() {

                        new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        new GetData().execute(Const.PROFILE_TIMELINE_URL + feedModel.getUserID() + "&" + Const.LIMIT + "=" + 100 + "&" + Const.OFFSET + "=" + 1);

                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            ArrayList<Double> resultList = null;
            HttpURLConnection connection = null;
            StringBuilder jsonResults = new StringBuilder();
            String result = "";

            try {
                StringBuilder sb = new StringBuilder(params[0]);

                URL url = new URL(sb.toString());
                Log.d(Const.LOG_TAG, url.toString());
                connection = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(connection.getInputStream());

                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            result = jsonResults.toString();
            return result;
        }

        @Override
        protected void onPostExecute(String doubles) {

//            Log.d(Const.LOG_TAG, encodedImage);

            try{

                JSONObject jsonObject = new JSONObject(doubles);
                Log.d(Const.LOG_TAG, jsonObject.toString());
                String result = jsonObject.getString("result");
                if (result.equalsIgnoreCase("1")) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<FeedModel> arrayList = new ArrayList<FeedModel>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String content = item.getString(Const.POST_CONTENT);
                        String profileImage = item.getString(Const.PROFILE_IMAGE);
                        String nickname = item.getString(Const.NICK_NAME);
                        String itemID = item.getString(Const.ITEMID);

                        Log.d(Const.LOG_TAG, content + " - " + profileImage + " - " + nickname);

                        FeedModel model = new FeedModel(profileImage, nickname, content, itemID);

                        arrayList.add(model);
                    }
                    arr.clear();
                    arr.addAll(arrayList);
                    adapter.notifyDataSetChanged();
//                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.setLoaded();
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void postFootPrint(final String footprintID, String visiter){
        class PostFootPrint extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    if(result.equalsIgnoreCase("1")){
                        Log.d(Const.LOG_TAG, "post footprint success");
                    }else{
                        Log.d(Const.LOG_TAG, "post footprint failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("footprintID",params[0]);
                data.put("visitor",params[1]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.POST_FOOT_PRINT_URL,data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        PostFootPrint getData = new PostFootPrint();
        getData.execute(footprintID, visiter);
    }
}
