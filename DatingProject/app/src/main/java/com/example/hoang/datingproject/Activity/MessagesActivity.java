package com.example.hoang.datingproject.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoang.datingproject.Adapter.FeedsAdapter;
import com.example.hoang.datingproject.Adapter.InboxAdapter;
import com.example.hoang.datingproject.Fragment.EmotionFragment;
import com.example.hoang.datingproject.Fragment.FeedsFragment;
import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.ImageItem;
import com.example.hoang.datingproject.Model.InboxModel;
import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.PersonModel;
import com.example.hoang.datingproject.R;
import com.example.hoang.datingproject.Utilities.Const;
import com.example.hoang.datingproject.Utilities.FontManager;
import com.example.hoang.datingproject.Utilities.RegisterUserClass;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener, EmotionFragment.ListSelectionListener{

    private EditText message_edittext;
    private TextView back_button, ban_button;
    private Button message_button;
    private Button message_camera, message_emotion;
    private RecyclerView recyclerView;
    private InboxAdapter adapter;
    private ArrayList<InboxModel> arr = new ArrayList<InboxModel>();

    private FragmentManager mFragmentManager;
    private FrameLayout mEmotionsFrameLayout;
    private EmotionFragment emotionFragment = new EmotionFragment();
    private static int count;
    private FragmentTransaction fragmentTransaction;
    private Bitmap img = null;
    private ArrayList<Integer> imgId = new ArrayList<Integer>();
    private PersonModel friend = new PersonModel();
    private MediaPlayer media;
    private String receiverProfile;
    private String receiverID;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Const.SOCKET_URL);
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        getControls();
    }

    private void getControls() {

        Intent intent = getIntent();

        int from = intent.getIntExtra("from", 0);

        if (from == Const.FROM_FRIEND_ADAPTER) {
            friend = (PersonModel) intent.getSerializableExtra("model");
            receiverProfile = friend.getImage();
            receiverID = friend.getId();
            Log.d(Const.LOG_TAG, "vao 1");
        } else if (from == Const.FROM_FEED_ADAPTER) {
            FeedModel feedModel = (FeedModel) intent.getSerializableExtra("model");
            receiverProfile = feedModel.getFeedIcon();
            receiverID = feedModel.getUserID();
            Log.d(Const.LOG_TAG, "vao 2");
        } else if (from == Const.FROM_MESSAGE_ADAPTER) {
            MessageModel messageModel = (MessageModel) intent.getSerializableExtra("model");
            receiverProfile = messageModel.getMessage_icon();
            receiverID = messageModel.getFriend_id();
            Log.d(Const.LOG_TAG, "vao 3");
        }

        String senderId = PersonalInfoActivity.getDefaults("id", MessagesActivity.this);

        mSocket.connect();
        mSocket.emit("client-gui-username", senderId);
        mSocket.on("ketquaDangKyUn", onNewMessage_DangKyUserName);

        message_edittext = (EditText) findViewById(R.id.message_edittext);
        message_camera = (Button) findViewById(R.id.message_camera);
        back_button = (TextView) findViewById(R.id.back_button);
        ban_button = (TextView) findViewById(R.id.ban_button);
        message_emotion = (Button) findViewById(R.id.message_emotion);
        message_button = (Button) findViewById(R.id.message_button);
        Typeface font = FontManager.getTypeface(MessagesActivity.this, FontManager.FONTAWESOME);
        message_camera.setTypeface(font);
        back_button.setTypeface(font);
        ban_button.setTypeface(font);
        message_emotion.setTypeface(font);

        message_camera.setOnClickListener(this);
        message_emotion.setOnClickListener(this);
        message_button.setOnClickListener(this);
        back_button.setOnClickListener(this);

        registerForContextMenu(message_camera);

        count = 0;

        mEmotionsFrameLayout = (FrameLayout) findViewById(R.id.emotion_fragment_container);
        // Get a reference to the FragmentManager
        mFragmentManager = getSupportFragmentManager();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        arr.clear();
//        arr.addAll(getData());
        adapter = new InboxAdapter(MessagesActivity.this, arr);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessagesActivity.this, LinearLayoutManager.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),4);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        mSocket.emit("client-chat-client", receiverID);
        mSocket.on("updatechat", onNewMessage_UpdateChat);
        mSocket.on("updatechatimage", onNewMessage_UpdateChatImage);
//        scrollToTop();
//        recyclerView.setHasFixedSize(true);
        new GetData().execute(Const.CHAT_LOG_URL + "&id1=" + PersonalInfoActivity.getDefaults("id", MessagesActivity.this) + "&id2=" + receiverID);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_button:
                if (message_edittext.getText().toString().equals("")){
                    Toast.makeText(MessagesActivity.this, "empty messages", Toast.LENGTH_SHORT).show();
                }else {
                    mSocket.emit("client-gui-chat", message_edittext.getText().toString());
                    sendChat(PersonalInfoActivity.getDefaults("id", MessagesActivity.this), receiverID, message_edittext.getText().toString(),PersonalInfoActivity.getDefaults("id", MessagesActivity.this));
                    Log.d(Const.LOG_TAG, PersonalInfoActivity.getDefaults("id", MessagesActivity.this) + receiverID + message_edittext.getText().toString() + PersonalInfoActivity.getDefaults("id", MessagesActivity.this));
                    message_edittext.setText("");
                }
                break;
            case R.id.back_button:
                finish();
                break;
            case R.id.message_emotion:
                break;
        }
    }

    private void addEmotionFragment() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mEmotionsFrameLayout.setLayoutParams(layoutParams);
//                    mQuotesFrameLayout.setBackgroundResource(R.color.gray);
        fragmentTransaction = mFragmentManager
                .beginTransaction();
        fragmentTransaction.add(R.id.emotion_fragment_container,
                emotionFragment, "emotion");
        fragmentTransaction.addToBackStack(null);

        // Commit the FragmentTransaction
        fragmentTransaction.commit();
    }

    private void removeEmotionFragment() {
        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentByTag("emotion")).commit();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        mEmotionsFrameLayout.setLayoutParams(layoutParams);
    }

    @Override
    public void onBackPressed() {
        if(count % 2 !=0){
            removeEmotionFragment();
            count++;
        }else {
            finish();
        }
    }

    @Override
    public void onListSelection(int index, ArrayList<ImageItem> arr) {

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
        InboxModel model;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
        Date c = Calendar.getInstance().getTime();
        String newFormat = sdf.format(c);
        String message;
        if (requestCode == Const.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                imageBitmap = getResizedBitmap(imageBitmap, 200);

                message = convertBitmapToString(imageBitmap);

                mSocket.emit("client-gui-image-chat", message);
                sendChat(PersonalInfoActivity.getDefaults("id", MessagesActivity.this), receiverID, message, PersonalInfoActivity.getDefaults("id", MessagesActivity.this));
//                scrollToTop();
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

                img = getResizedBitmap(img, 200);
                message = convertBitmapToString(img);
                mSocket.emit("client-gui-image-chat", message);
                sendChat(PersonalInfoActivity.getDefaults("id", MessagesActivity.this), receiverID, message, PersonalInfoActivity.getDefaults("id", MessagesActivity.this));
//                adapter.notifyDataSetChanged();
//                scrollToTop();
            }
        }
    }

    private String convertBitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object

        byte[] b = baos.toByteArray();
        String profileImage = Base64.encodeToString(b, Base64.DEFAULT);

        return profileImage;
    }

    private Bitmap convertStringtoBitmap(String image) {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
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

    private void scrollToTop() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MessagesActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
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
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String profileImage = item.getString(Const.PROFILE_IMAGE);
                        String content = item.getString("content");
                        String posttime = item.getString("posttime");
                        String fromID = item.getString("fromID");
                        InboxModel model;

                        if (content.length() > 500) {
                            Bitmap image = convertStringtoBitmap(content);
                            model = new InboxModel(posttime, image, fromID, profileImage);
                        } else {
                            model = new InboxModel(posttime, content, fromID, profileImage);
                        }

                        arr.add(model);
                        adapter.notifyDataSetChanged();
                    }
                    if (arr.size() != 0) {
                        scrollToTop();
                    }
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Emitter.Listener onNewMessage_DangKyUserName = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
//                    JSONObject dataid = (JSONObject) args[1];
                    String noidung;
                    String id;
                    try {
                        noidung = data.getString("noidung");
                        if (noidung == "true") {
                            Log.d(Const.LOG_TAG, "register socket success");
                        } else {
                            Log.d(Const.LOG_TAG, "register socket failed");
                        }
                    } catch (JSONException e) {
                        return;
                    }
                    // add the message to view
                }
            });
        }
    };

    private Emitter.Listener onNewMessage_UpdateChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    String imageMessage;
                    String userID;
                    String profileImage;
                    media = MediaPlayer.create(MessagesActivity.this, R.raw.ting);
                    media.start();

                    try {
                        userID = data.getString("username");
                        noidung = data.getString("message");

                        if (userID.equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", MessagesActivity.this))) {
                            profileImage = PersonalInfoActivity.getDefaults("profileImage", MessagesActivity.this);

                        } else {
                            profileImage = receiverProfile;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
                        Date c = Calendar.getInstance().getTime();
                        String newFormat = sdf.format(c);

                        InboxModel model = new InboxModel(newFormat, noidung, userID, profileImage);

                        arr.add(model);
                        adapter.notifyDataSetChanged();
                        scrollToTop();
                    } catch (JSONException e) {
                        return;
                    }
                    // add the message to view
                }
            });
        }
    };

    private Emitter.Listener onNewMessage_UpdateChatImage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String imageMessage;
                    String userID;
                    String profileImage;
                    media = MediaPlayer.create(MessagesActivity.this, R.raw.ting);
                    media.start();

                    try {
                        userID = data.getString("username");
                        imageMessage = data.getString("image");

                        Bitmap image = convertStringtoBitmap(imageMessage);

                        if (userID.equalsIgnoreCase(PersonalInfoActivity.getDefaults("id", MessagesActivity.this))) {
                            profileImage = PersonalInfoActivity.getDefaults("profileImage", MessagesActivity.this);

                        } else {
                            profileImage = receiverProfile;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
                        Date c = Calendar.getInstance().getTime();
                        String newFormat = sdf.format(c);

                        InboxModel model = new InboxModel(newFormat, image, userID, profileImage);
                        Log.d(Const.LOG_TAG, "send image successful");
                        arr.add(model);
                        adapter.notifyDataSetChanged();
                        scrollToTop();

                    } catch (JSONException e) {
                        return;
                    }
                    // add the message to view
                }
            });
        }
    };

    public void sendChat(final String userID1, String userID2, String contents, String from){
        class SendChat extends AsyncTask<String, Void, String> {

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
                        Log.d(Const.LOG_TAG, "send chat success");
                    }else{
                        Log.d(Const.LOG_TAG, "send chat failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put(Const.USERID1,params[0]);
                data.put(Const.USERID2,params[1]);
                data.put(Const.MESSAGE,params[2]);
                data.put(Const.FROM_USER,params[3]);

                RegisterUserClass ruc = new RegisterUserClass();

                String result = ruc.sendPostRequest(Const.CHAT_SEND_URL,data);

                Log.d(Const.LOG_TAG, result + "");
                return result;
            }
        }
        SendChat send = new SendChat();
        send.execute(userID1, userID2, contents, from);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("signout", PersonalInfoActivity.getDefaults("id", MessagesActivity.this));
        mSocket.disconnect();
    }
}
