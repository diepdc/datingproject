package com.example.hoang.datingproject.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.InboxModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NewNoteActivity extends AppCompatActivity {

    private TextView plus_icon, new_note_camera, new_note_tag, new_note_location, new_note_emotion, friend_icon, right_icon;
    private ImageView new_note_image, new_note_profile;
    private Bitmap img = null;
    private EditText description;
    private String image_des = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        getControls();
    }

    private void getControls() {
        plus_icon = (TextView) findViewById(R.id.plus_icon);
        new_note_camera = (TextView) findViewById(R.id.new_note_camera);
        new_note_tag = (TextView) findViewById(R.id.new_note_tag);
        new_note_emotion = (TextView) findViewById(R.id.new_note_emotion);
        new_note_location = (TextView) findViewById(R.id.new_note_location);
        friend_icon = (TextView) findViewById(R.id.friend_icon);
        right_icon = (TextView) findViewById(R.id.right_icon);
        description = (EditText) findViewById(R.id.new_note_description);

        Typeface font = FontManager.getTypeface(NewNoteActivity.this, FontManager.FONTAWESOME);
        plus_icon.setTypeface(font);
        new_note_camera.setTypeface(font);
        new_note_tag.setTypeface(font);
        new_note_emotion.setTypeface(font);
        new_note_location.setTypeface(font);
        friend_icon.setTypeface(font);
        right_icon.setTypeface(font);

        new_note_image = (ImageView) findViewById(R.id.new_note_image);

        new_note_profile = (ImageView) findViewById(R.id.new_post_profile);

        String profileImage = PersonalInfoActivity.getDefaults("profileImage", NewNoteActivity.this);

        byte[] decodedString = Base64.decode(profileImage, Base64.DEFAULT);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        new_note_profile.setImageBitmap(decodedByte);

        plus_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTimeline(PersonalInfoActivity.getDefaults("id", NewNoteActivity.this), description.getText().toString(), image_des );
            }
        });

        registerForContextMenu(new_note_camera);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.my_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.capture:
                dispatchTakePictureIntent();
                break;
            case R.id.gallery:
                getImageFromGallery();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Const.PICTURE_CHOOSE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Const.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FeedModel model;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
        Date c = Calendar.getInstance().getTime();
        String newFormat = sdf.format(c);
        if (requestCode == Const.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                img = imageBitmap;

                new_note_image.setImageBitmap(img);

                img = getResizedBitmap(img, 200);
                image_des = convertBitmapToString(img);
//                model = new FeedModel(newFormat,imageBitmap);
//                arr.add(model);
//                adapter.notifyDataSetChanged();
//                Toast.makeText(MessagesActivity.this, "Hello", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Const.PICTURE_CHOOSE && resultCode == RESULT_OK) {
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
                new_note_image.setImageBitmap(img);

                img = getResizedBitmap(img, 200);
                image_des = convertBitmapToString(img);

//                model = new InboxModel(newFormat,img);
//                arr.add(model);
//                adapter.notifyDataSetChanged();
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

    private String convertBitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object

        byte[] b = baos.toByteArray();
        String profileImage = Base64.encodeToString(b, Base64.DEFAULT);

        return profileImage;
    }

    public void postTimeline(final String userID, String contents, String image){
        class PostTimeline extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(NewNoteActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Posting...");
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
                        Log.d(Const.LOG_TAG, "post timeline success");
                    }else{
                        Log.d(Const.LOG_TAG, "post timeline failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("userID",params[0]);
                data.put("contents",params[1]);
                data.put("image", params[2]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.POST_TIMELINE_URL,data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        PostTimeline getData = new PostTimeline();
        getData.execute(userID, contents, image);
    }
}
