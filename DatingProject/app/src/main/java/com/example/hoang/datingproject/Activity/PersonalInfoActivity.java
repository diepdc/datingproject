package com.example.hoang.datingproject.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Model.PersonModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView exit_button, save_button;
    private EditText editInfo, nickname, old;
    private Button bt1, bt2, bt3;
    private ImageView avatar;
    private String gender = "";
    private Bitmap img = null;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        getControls();
    }

    public void getControls() {
        Firebase.setAndroidContext(PersonalInfoActivity.this);
        ref = new Firebase(Const.FIRE_BASE_URL);
        Typeface font = FontManager.getTypeface(PersonalInfoActivity.this, FontManager.FONTAWESOME);

        editInfo = (EditText) findViewById(R.id.editInfo);
        nickname = (EditText) findViewById(R.id.nickname);
        old = (EditText) findViewById(R.id.old);

        save_button = (TextView) findViewById(R.id.save);
        exit_button = (TextView) findViewById(R.id.exit_button);
        exit_button.setTypeface(font);
        editInfo.setText(R.string.personal_info_item13);

        avatar = (ImageView) findViewById(R.id.avatar);

        bt1 = (Button) findViewById(R.id.personal_btn1);
        bt2 = (Button) findViewById(R.id.personal_btn2);
        bt3 = (Button) findViewById(R.id.personal_btn3);

        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        avatar.setOnClickListener(this);
        exit_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
        editInfo.setOnClickListener(this);

        bt1.setSelected(true);
        bt1.setPressed(true);

        new GetProfileData().execute(Const.USER_PROFILE_URL + PersonalInfoActivity.getDefaults("id", PersonalInfoActivity.this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personal_btn1:
                bt1.setSelected(true);
                bt1.setPressed(true);
                bt2.setSelected(false);
                bt2.setPressed(false);
                bt3.setSelected(false);
                bt3.setPressed(false);
                gender = "male";
                break;
            case R.id.personal_btn2:
                bt1.setSelected(false);
                bt1.setPressed(false);
                bt2.setSelected(true);
                bt2.setPressed(true);
                bt3.setSelected(false);
                bt3.setPressed(false);
                gender = "female";
                break;
            case R.id.personal_btn3:
                bt1.setSelected(false);
                bt1.setPressed(false);
                bt2.setSelected(false);
                bt2.setPressed(false);
                bt3.setSelected(true);
                bt3.setPressed(true);
                gender = "gay/les";
                break;
            case R.id.exit_button:
                finish();
                break;
            case R.id.avatar:
                getImageFromGallery();
                break;
            case R.id.save:
                String id = PersonalInfoActivity.getDefaults("id", PersonalInfoActivity.this);

                if (id.equalsIgnoreCase("123")) {
                    String name = nickname.getText().toString();
                    String birthday = old.getText().toString();
                    String description = editInfo.getText().toString();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (img != null) {
                        img.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    } else {
                        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.avatar);
                        icon.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    }

                    byte[] b = baos.toByteArray();
                    String profileImage = Base64.encodeToString(b, Base64.DEFAULT);

                    createUser(name, profileImage, birthday, gender, description);
                } else {
                    String name = nickname.getText().toString();
                    String birthday = old.getText().toString();
                    String description = editInfo.getText().toString();
                    String profileImage = PersonalInfoActivity.getDefaults("profileImage", this);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    if (img != null) {
                        img.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        profileImage = Base64.encodeToString(b, Base64.DEFAULT);
                    }

                    updateUser(name, profileImage, birthday, gender, description);
                }
                break;
        }
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.PROFILE_CHOOSE);
    }

    public void createUser(final String nickname, String profileImage, String old, String gender, String description){
        class CreateUser extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(PersonalInfoActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    if(result.equalsIgnoreCase("1")){
                        Log.d(Const.LOG_TAG, "create user success");
                        String userId = jsonObject.getString("userId");
                        String profileImage = jsonObject.getString("profileImage");

                        setDefault("profileImage", profileImage, PersonalInfoActivity.this);
                        setDefault("id", userId, PersonalInfoActivity.this);

                        Log.d(Const.LOG_TAG, userId + "-" + profileImage);

                        Toast.makeText(PersonalInfoActivity.this, "create profile success", Toast.LENGTH_SHORT).show();

                    }else{
                        Log.d(Const.LOG_TAG, "create failed");
                        Toast.makeText(PersonalInfoActivity.this, "create profile failed", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(Const.NICK_NAME,params[0]);
                data.put(Const.PROFILE_IMAGE,params[1]);
                data.put(Const.BIRTHDAY, params[2]);
                data.put(Const.GENDER, params[3]);
                data.put(Const.DESCRIPTION, params[4]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.USER_CREATE_URL,data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        CreateUser getData = new CreateUser();
        getData.execute(nickname, profileImage, old, gender, description);
    }

    public void updateUser(final String nickname, String profileImage, String old, String gender, String description){
        class UpdateUser extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(PersonalInfoActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating Profile...");
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String result = jsonObject.getString("result");
                    if(result.equalsIgnoreCase("1")){
                        Log.d(Const.LOG_TAG, "update user success");

                        Toast.makeText(PersonalInfoActivity.this, "update profile success", Toast.LENGTH_SHORT).show();

                    }else{
                        Log.d(Const.LOG_TAG, "update profile failed");
                        Toast.makeText(PersonalInfoActivity.this, "update profile failed", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(Const.NICK_NAME,params[0]);
                data.put(Const.PROFILE_IMAGE,params[1]);
                data.put(Const.BIRTHDAY, params[2]);
                data.put(Const.GENDER, params[3]);
                data.put(Const.DESCRIPTION, params[4]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.USER_PROFILE_UPDATE_URL + getDefaults("id", PersonalInfoActivity.this),data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        UpdateUser getData = new UpdateUser();
        getData.execute(nickname, profileImage, old, gender, description);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.PROFILE_CHOOSE && resultCode == RESULT_OK) {
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

                img = getResizedBitmap(img, 200);

                BitmapDrawable ob = new BitmapDrawable(getResources(), img);
                avatar.setBackground(ob);
            }
        }
    }

    public static void setDefault(String key, String value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "123");
    }

    private class GetProfileData extends AsyncTask<String, Void, String> {

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
            try{

                JSONObject jsonObject = new JSONObject(doubles);
                Log.d(Const.LOG_TAG, jsonObject.toString());
                String result = jsonObject.getString("result");
                if (result.equalsIgnoreCase("1")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String gend = data.getString(Const.GENDER);
                    String profileImage = data.getString(Const.PROFILE_IMAGE);
                    String name = data.getString(Const.NICK_NAME);
                    String birthday = data.getString(Const.BIRTHDAY);
                    String description = data.getString(Const.DESCRIPTION);
                    String userID = data.getString(Const.USERID);

                    byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    BitmapDrawable ob = new BitmapDrawable(getResources(), decodedByte);
                    avatar.setBackground(ob);
                    nickname.setText(name);
                    old.setText(birthday);
                    editInfo.setText(description);

                    if (gend.equals("male")) {
                        bt1.setSelected(true);
                        bt1.setPressed(true);
                        bt2.setSelected(false);
                        bt2.setPressed(false);
                        bt3.setSelected(false);
                        bt3.setPressed(false);
                    } else if (gend.equals("female")) {
                        bt1.setSelected(false);
                        bt1.setPressed(false);
                        bt2.setSelected(true);
                        bt2.setPressed(true);
                        bt3.setSelected(false);
                        bt3.setPressed(false);
                    }else {
                        bt1.setSelected(false);
                        bt1.setPressed(false);
                        bt2.setSelected(false);
                        bt2.setPressed(false);
                        bt3.setSelected(true);
                        bt3.setPressed(true);
                    }

                    setDefault("profileImage", profileImage, PersonalInfoActivity.this);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
