package com.example.hoang.datingproject.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;

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

public class SettingsActivity extends AppCompatActivity {

    private TextView delete_button, exit_button, right_arrow_button, right_arrow_button1, right_arrow_button2, right_arrow_button3;
    private RelativeLayout personalInfo, feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getControls();
    }

    private void getControls() {
        exit_button = (TextView) findViewById(R.id.exit_button);
        right_arrow_button = (TextView) findViewById(R.id.right_arrow_button);
        right_arrow_button1 = (TextView) findViewById(R.id.right_arrow_button1);
        right_arrow_button2 = (TextView) findViewById(R.id.right_arrow_button2);
        right_arrow_button3 = (TextView) findViewById(R.id.right_arrow_button3);
        delete_button = (TextView) findViewById(R.id.deleteUser);

        personalInfo = (RelativeLayout) findViewById(R.id.personalInfo);
        feedback = (RelativeLayout) findViewById(R.id.feedback);

        Typeface font = FontManager.getTypeface(SettingsActivity.this, FontManager.FONTAWESOME);

        exit_button.setTypeface(font);
        right_arrow_button.setTypeface(font);
        right_arrow_button1.setTypeface(font);
        right_arrow_button2.setTypeface(font);
        right_arrow_button3.setTypeface(font);

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, FeedbackActivity.class);
                startActivity(intent);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteUser().execute(Const.USER_PROFILE_DELETE_URL + PersonalInfoActivity.getDefaults("id", SettingsActivity.this));
            }
        });
    }

    private class DeleteUser extends AsyncTask<String, Void, String> {

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
                    Log.d(Const.LOG_TAG, "delete user successful!");
                    Toast.makeText(SettingsActivity.this, "delete user successful!", Toast.LENGTH_SHORT).show();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    Bitmap icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.avatar);
                    icon.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object

                    byte[] b = baos.toByteArray();
                    String profileImage = Base64.encodeToString(b, Base64.DEFAULT);

                    PersonalInfoActivity.setDefault("id", "123", SettingsActivity.this);
                    PersonalInfoActivity.setDefault("profileImage", profileImage, SettingsActivity.this);
                } else {
                    Log.d(Const.LOG_TAG, "delete user failed!");
                    Toast.makeText(SettingsActivity.this, "delete user failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
